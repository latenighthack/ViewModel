package com.latenighthack.viewmodel

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.isInterface
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * Transform exampleViewModel.create<TestApi>() to exampleViewModel.create<TestApi>(_TestApiProvider())
 */
internal class CreateFuncTransformer(
    private val pluginContext: IrPluginContext,
    private val debugLogger: DebugLogger,
) : IrElementTransformerVoidWithContext() {
    companion object {
        fun errorTypeArgumentNotInterface(implName: String) =
            "create<$implName> argument is not supported. Type argument needs to be an interface"

        fun errorImplNotFound(
            implName: String,
            className: String,
        ) = "$implName not found, did you apply the Ksp ViewModel plugin? Use .create$className() instead"

        fun errorClassNotFound(implName: String) = "class $implName not found, did you apply the Ksp ViewModel plugin?"

        private const val KTORFIT_PACKAGE = "com.latenighthack.viewmodel.ViewModel"
        private const val KTORFIT_CREATE = "create"
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitExpression(expression: IrExpression): IrExpression {
        // Find exampleViewModel.create<TestApi>()
        (expression as? IrCall)?.let { irCall ->
            if (irCall.typeArgumentsCount > 0) {
                if (!expression.symbol.owner.symbol.toString().contains(KTORFIT_PACKAGE)) {
                    return expression
                }
                if (expression.symbol.owner.name.asString() != KTORFIT_CREATE) {
                    return expression
                }

                if (expression.getValueArgument(0) != null) {
                    return expression
                }

                // Get T from create<T>()
                val argumentType = irCall.getTypeArgument(0) ?: return expression
                val classFqName = argumentType.classFqName

                if (!argumentType.isInterface()) {
                    throw IllegalStateException(
                        errorTypeArgumentNotInterface(argumentType.originalKotlinType.toString()),
                    )
                }

                if (classFqName == null) {
                    throw IllegalStateException(errorClassNotFound(argumentType.originalKotlinType.toString()))
                }

                val packageName = classFqName.packageName
                val className = classFqName.shortName().toString()
                val providerClassName = "_$className" + "Provider"

                // Find the class _TestApiProvider
                val implClassSymbol =
                    pluginContext.referenceClass(
                        ClassId(
                            FqName(packageName),
                            Name.identifier(providerClassName),
                        ),
                    ) ?: throw IllegalStateException(errorImplNotFound(providerClassName, className))

                val newConstructor = implClassSymbol.constructors.first()

                // Create the constructor call for _ExampleApiProvider()
                val newCall =
                    IrConstructorCallImpl(
                        0,
                        0,
                        type = implClassSymbol.defaultType,
                        symbol = newConstructor,
                        0,
                        0,
//                        0,
//                        null,
                    )

                // Set _ExampleApiProvider() as argument for create<ExampleApi>()
                irCall.putValueArgument(0, newCall)
                debugLogger.log(
                    "Transformed " + argumentType.originalKotlinType.toString() + " to _$className" + "Provider",
                )
                return super.visitExpression(irCall)
            }
        }
        return super.visitExpression(expression)
    }
}

private val FqName?.packageName: String
    get() {
        return this.toString().substringBeforeLast(".")
    }

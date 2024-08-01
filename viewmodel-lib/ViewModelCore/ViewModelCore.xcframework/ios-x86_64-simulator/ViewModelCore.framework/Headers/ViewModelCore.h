#import <Foundation/NSArray.h>
#import <Foundation/NSDictionary.h>
#import <Foundation/NSError.h>
#import <Foundation/NSObject.h>
#import <Foundation/NSSet.h>
#import <Foundation/NSString.h>
#import <Foundation/NSValue.h>

@class UICollectionViewCell, UITableViewCell, UITableView, UICollectionView, VMCChange, VMCFlowListDataSourceSectionedChange, VMCNavigatorArgs, VMCGlobalDispatcher, VMCKotlinThrowable, VMCKotlinIntRange, VMCChangeDeleted, VMCChangeInserted, VMCChangeMoved, VMCChangeRefresh, VMCChangeReloaded, VMCDeltaCompanion, VMCDelta<T>, VMCKotlinByteIterator, VMCKotlinByteArray, VMCKotlinArray<T>, VMCCFlow<T>, NSData, VMCKotlinException, VMCKotlinRuntimeException, VMCKotlinIllegalStateException, VMCKotlinCancellationException, VMCKotlinIntProgressionCompanion, VMCKotlinIntIterator, VMCKotlinIntProgression, VMCKotlinIntRangeCompanion, VMCKotlinKTypeProjection, VMCKotlinUnit, VMCKotlinKVariance, VMCKotlinKTypeProjectionCompanion, VMCKotlinEnumCompanion, VMCKotlinEnum<E>;

@protocol VMCKotlinx_coroutines_coreCoroutineScope, VMCKotlinx_coroutines_coreFlow, VMCFlowCloseable, VMCViewModel, VMCStoredProperty, VMCKotlinKProperty, VMCKotlinSuspendFunction1, VMCKotlinCoroutineContext, VMCKotlinx_coroutines_coreCompletableJob, VMCKotlinx_coroutines_coreFlowCollector, VMCKotlinSuspendFunction0, VMCKotlinKType, VMCKotlinKAnnotatedElement, VMCKotlinKCallable, VMCKotlinFunction, VMCKotlinCoroutineContextElement, VMCKotlinCoroutineContextKey, VMCKotlinx_coroutines_coreChildHandle, VMCKotlinx_coroutines_coreChildJob, VMCKotlinx_coroutines_coreDisposableHandle, VMCKotlinx_coroutines_coreJob, VMCKotlinSequence, VMCKotlinx_coroutines_coreSelectClause0, VMCKotlinIterator, VMCKotlinIterable, VMCKotlinClosedRange, VMCKotlinOpenEndRange, VMCKotlinKClassifier, VMCKotlinx_coroutines_coreParentJob, VMCKotlinx_coroutines_coreSelectInstance, VMCKotlinx_coroutines_coreSelectClause, VMCKotlinComparable;

NS_ASSUME_NONNULL_BEGIN
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunknown-warning-option"
#pragma clang diagnostic ignored "-Wincompatible-property-type"
#pragma clang diagnostic ignored "-Wnullability"

#pragma push_macro("_Nullable_result")
#if !__has_feature(nullability_nullable_result)
#undef _Nullable_result
#define _Nullable_result _Nullable
#endif

__attribute__((swift_name("KotlinBase")))
@interface VMCBase : NSObject
- (instancetype)init __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
+ (void)initialize __attribute__((objc_requires_super));
@end

@interface VMCBase (VMCBaseCopying) <NSCopying>
@end

__attribute__((swift_name("KotlinMutableSet")))
@interface VMCMutableSet<ObjectType> : NSMutableSet<ObjectType>
@end

__attribute__((swift_name("KotlinMutableDictionary")))
@interface VMCMutableDictionary<KeyType, ObjectType> : NSMutableDictionary<KeyType, ObjectType>
@end

@interface NSError (NSErrorVMCKotlinException)
@property (readonly) id _Nullable kotlinException;
@end

__attribute__((swift_name("KotlinNumber")))
@interface VMCNumber : NSNumber
- (instancetype)initWithChar:(char)value __attribute__((unavailable));
- (instancetype)initWithUnsignedChar:(unsigned char)value __attribute__((unavailable));
- (instancetype)initWithShort:(short)value __attribute__((unavailable));
- (instancetype)initWithUnsignedShort:(unsigned short)value __attribute__((unavailable));
- (instancetype)initWithInt:(int)value __attribute__((unavailable));
- (instancetype)initWithUnsignedInt:(unsigned int)value __attribute__((unavailable));
- (instancetype)initWithLong:(long)value __attribute__((unavailable));
- (instancetype)initWithUnsignedLong:(unsigned long)value __attribute__((unavailable));
- (instancetype)initWithLongLong:(long long)value __attribute__((unavailable));
- (instancetype)initWithUnsignedLongLong:(unsigned long long)value __attribute__((unavailable));
- (instancetype)initWithFloat:(float)value __attribute__((unavailable));
- (instancetype)initWithDouble:(double)value __attribute__((unavailable));
- (instancetype)initWithBool:(BOOL)value __attribute__((unavailable));
- (instancetype)initWithInteger:(NSInteger)value __attribute__((unavailable));
- (instancetype)initWithUnsignedInteger:(NSUInteger)value __attribute__((unavailable));
+ (instancetype)numberWithChar:(char)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedChar:(unsigned char)value __attribute__((unavailable));
+ (instancetype)numberWithShort:(short)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedShort:(unsigned short)value __attribute__((unavailable));
+ (instancetype)numberWithInt:(int)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedInt:(unsigned int)value __attribute__((unavailable));
+ (instancetype)numberWithLong:(long)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedLong:(unsigned long)value __attribute__((unavailable));
+ (instancetype)numberWithLongLong:(long long)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedLongLong:(unsigned long long)value __attribute__((unavailable));
+ (instancetype)numberWithFloat:(float)value __attribute__((unavailable));
+ (instancetype)numberWithDouble:(double)value __attribute__((unavailable));
+ (instancetype)numberWithBool:(BOOL)value __attribute__((unavailable));
+ (instancetype)numberWithInteger:(NSInteger)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedInteger:(NSUInteger)value __attribute__((unavailable));
@end

__attribute__((swift_name("KotlinByte")))
@interface VMCByte : VMCNumber
- (instancetype)initWithChar:(char)value;
+ (instancetype)numberWithChar:(char)value;
@end

__attribute__((swift_name("KotlinUByte")))
@interface VMCUByte : VMCNumber
- (instancetype)initWithUnsignedChar:(unsigned char)value;
+ (instancetype)numberWithUnsignedChar:(unsigned char)value;
@end

__attribute__((swift_name("KotlinShort")))
@interface VMCShort : VMCNumber
- (instancetype)initWithShort:(short)value;
+ (instancetype)numberWithShort:(short)value;
@end

__attribute__((swift_name("KotlinUShort")))
@interface VMCUShort : VMCNumber
- (instancetype)initWithUnsignedShort:(unsigned short)value;
+ (instancetype)numberWithUnsignedShort:(unsigned short)value;
@end

__attribute__((swift_name("KotlinInt")))
@interface VMCInt : VMCNumber
- (instancetype)initWithInt:(int)value;
+ (instancetype)numberWithInt:(int)value;
@end

__attribute__((swift_name("KotlinUInt")))
@interface VMCUInt : VMCNumber
- (instancetype)initWithUnsignedInt:(unsigned int)value;
+ (instancetype)numberWithUnsignedInt:(unsigned int)value;
@end

__attribute__((swift_name("KotlinLong")))
@interface VMCLong : VMCNumber
- (instancetype)initWithLongLong:(long long)value;
+ (instancetype)numberWithLongLong:(long long)value;
@end

__attribute__((swift_name("KotlinULong")))
@interface VMCULong : VMCNumber
- (instancetype)initWithUnsignedLongLong:(unsigned long long)value;
+ (instancetype)numberWithUnsignedLongLong:(unsigned long long)value;
@end

__attribute__((swift_name("KotlinFloat")))
@interface VMCFloat : VMCNumber
- (instancetype)initWithFloat:(float)value;
+ (instancetype)numberWithFloat:(float)value;
@end

__attribute__((swift_name("KotlinDouble")))
@interface VMCDouble : VMCNumber
- (instancetype)initWithDouble:(double)value;
+ (instancetype)numberWithDouble:(double)value;
@end

__attribute__((swift_name("KotlinBoolean")))
@interface VMCBoolean : VMCNumber
- (instancetype)initWithBool:(BOOL)value;
+ (instancetype)numberWithBool:(BOOL)value;
@end

__attribute__((swift_name("FlowListDataSource")))
@interface VMCFlowListDataSource<T, U> : VMCBase
- (instancetype)initWithScope:(id<VMCKotlinx_coroutines_coreCoroutineScope>)scope items:(NSArray<id<VMCKotlinx_coroutines_coreFlow>> *)items __attribute__((swift_name("init(scope:items:)"))) __attribute__((objc_designated_initializer));
- (void)afterUpdate __attribute__((swift_name("afterUpdate()")));
- (id<VMCFlowCloseable>)attachCollectionViewCell:(UICollectionViewCell *)collectionViewCell model:(id<VMCViewModel>)model __attribute__((swift_name("attach(collectionViewCell:model:)")));
- (id<VMCFlowCloseable>)attachCell:(UITableViewCell *)cell model:(id<VMCViewModel>)model __attribute__((swift_name("attach(cell:model:)")));

/**
 * @note annotations
 *   kotlin.experimental.ExperimentalNativeApi
*/
- (void)bindToTable:(UITableView *)toTable __attribute__((swift_name("bind(toTable:)")));

/**
 * @note annotations
 *   kotlin.experimental.ExperimentalNativeApi
*/
- (void)bindCollectionView:(UICollectionView *)collectionView completionCallback:(void (^)(void))completionCallback breakpointCallback:(void (^)(NSString *))breakpointCallback __attribute__((swift_name("bind(collectionView:completionCallback:breakpointCallback:)")));
- (void)cellStateDidChangeCell:(UITableViewCell *)cell model:(id<VMCViewModel>)model state:(id)state __attribute__((swift_name("cellStateDidChange(cell:model:state:)")));
- (void)collectionViewCellStateDidChangeCell:(UICollectionViewCell *)cell model:(id<VMCViewModel>)model state:(id)state __attribute__((swift_name("collectionViewCellStateDidChange(cell:model:state:)")));
- (id _Nullable)currentItemInSectionSection:(int32_t)section index:(int32_t)index __attribute__((swift_name("currentItemInSection(section:index:)")));
- (int32_t)getItemsInSectionIndex:(int32_t)index __attribute__((swift_name("getItemsInSection(index:)")));
- (int32_t)getSectionCount __attribute__((swift_name("getSectionCount()")));
@property (readonly) NSArray<id<VMCKotlinx_coroutines_coreFlow>> *items __attribute__((swift_name("items")));
@property (readonly) id<VMCKotlinx_coroutines_coreCoroutineScope> scope __attribute__((swift_name("scope")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FlowListDataSourceSectionedChange")))
@interface VMCFlowListDataSourceSectionedChange : VMCBase
- (instancetype)initWithSection:(int32_t)section change:(VMCChange *)change __attribute__((swift_name("init(section:change:)"))) __attribute__((objc_designated_initializer));
- (VMCFlowListDataSourceSectionedChange *)doCopySection:(int32_t)section change:(VMCChange *)change __attribute__((swift_name("doCopy(section:change:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) VMCChange *change __attribute__((swift_name("change")));
@property (readonly) int32_t section __attribute__((swift_name("section")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FlowStateCollector")))
@interface VMCFlowStateCollector<T, U> : VMCBase
- (instancetype)initWithScope:(id<VMCKotlinx_coroutines_coreCoroutineScope>)scope items:(id<VMCKotlinx_coroutines_coreFlow>)items __attribute__((swift_name("init(scope:items:)"))) __attribute__((objc_designated_initializer));
- (T _Nullable)getIndex:(int32_t)index __attribute__((swift_name("get(index:)")));
- (U _Nullable)getStateIndex:(int32_t)index __attribute__((swift_name("getState(index:)")));
- (void)start __attribute__((swift_name("start()")));
- (void)stop __attribute__((swift_name("stop()")));
@property (readonly) id<VMCKotlinx_coroutines_coreFlow> items __attribute__((swift_name("items")));
@property (readonly) id<VMCKotlinx_coroutines_coreCoroutineScope> scope __attribute__((swift_name("scope")));
@property (readonly) int32_t size __attribute__((swift_name("size")));
@end

__attribute__((swift_name("ViewModel")))
@protocol VMCViewModel
@required
@property (readonly) id _Nullable initialState __attribute__((swift_name("initialState")));
@property (readonly) id<VMCKotlinx_coroutines_coreFlow> state __attribute__((swift_name("state")));
@end

__attribute__((swift_name("NavigableViewModel")))
@protocol VMCNavigableViewModel <VMCViewModel>
@required
@property (readonly) VMCNavigatorArgs *args __attribute__((swift_name("args")));
@end

__attribute__((swift_name("NavigatorArgs")))
@interface VMCNavigatorArgs : VMCBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));

/**
 * @note This method has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
- (id<VMCStoredProperty>)storedProperty __attribute__((swift_name("storedProperty()")));
@end

__attribute__((swift_name("StoredProperty")))
@protocol VMCStoredProperty
@required
- (id _Nullable)getValueThisRef:(id _Nullable)thisRef property:(id<VMCKotlinKProperty>)property __attribute__((swift_name("getValue(thisRef:property:)")));
- (void)setValueThisRef:(id _Nullable)thisRef property:(id<VMCKotlinKProperty>)property value:(id _Nullable)value __attribute__((swift_name("setValue(thisRef:property:value:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("NavigatorArgsInMemoryStoredProperty")))
@interface VMCNavigatorArgsInMemoryStoredProperty<T> : VMCBase <VMCStoredProperty>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (T _Nullable)getValueThisRef:(id _Nullable)thisRef property:(id<VMCKotlinKProperty>)property __attribute__((swift_name("getValue(thisRef:property:)")));
- (void)setValueThisRef:(id _Nullable)thisRef property:(id<VMCKotlinKProperty>)property value:(T _Nullable)value __attribute__((swift_name("setValue(thisRef:property:value:)")));
@end

__attribute__((swift_name("StatefulViewModel")))
@interface VMCStatefulViewModel<State> : VMCBase <VMCViewModel>
- (instancetype)initWithInitialState:(State _Nullable)initialState __attribute__((swift_name("init(initialState:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
 * @note This method has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
- (void)updateUpdater:(id<VMCKotlinSuspendFunction1>)updater completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("update(updater:completionHandler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
 * @note This method has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
- (void)withStateInspector:(id<VMCKotlinSuspendFunction1>)inspector completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("withState(inspector:completionHandler:)")));
@property (readonly) State _Nullable initialState __attribute__((swift_name("initialState")));
@property (readonly) id<VMCKotlinx_coroutines_coreFlow> state __attribute__((swift_name("state")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreCoroutineScope")))
@protocol VMCKotlinx_coroutines_coreCoroutineScope
@required
@property (readonly) id<VMCKotlinCoroutineContext> coroutineContext __attribute__((swift_name("coroutineContext")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("BindingScope")))
@interface VMCBindingScope : VMCBase <VMCKotlinx_coroutines_coreCoroutineScope>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (void)cancel __attribute__((swift_name("cancel()")));
- (void)collectFlow:(id<VMCKotlinx_coroutines_coreFlow>)flow collector:(void (^)(id))collector __attribute__((swift_name("collect(flow:collector:)")));
@property (readonly) id<VMCKotlinCoroutineContext> coroutineContext __attribute__((swift_name("coroutineContext")));
@property (readonly) id<VMCKotlinx_coroutines_coreCompletableJob> job __attribute__((swift_name("job")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreFlow")))
@protocol VMCKotlinx_coroutines_coreFlow
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)collectCollector:(id<VMCKotlinx_coroutines_coreFlowCollector>)collector completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("collect(collector:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("CFlow")))
@interface VMCCFlow<T> : VMCBase <VMCKotlinx_coroutines_coreFlow>
- (instancetype)initWithOrigin:(id<VMCKotlinx_coroutines_coreFlow>)origin __attribute__((swift_name("init(origin:)"))) __attribute__((objc_designated_initializer));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)collectCollector:(id<VMCKotlinx_coroutines_coreFlowCollector>)collector completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("collect(collector:completionHandler:)")));
- (id<VMCFlowCloseable>)watchBlock:(void (^)(T _Nullable))block __attribute__((swift_name("watch(block:)")));
@end

__attribute__((swift_name("FlowCloseable")))
@protocol VMCFlowCloseable
@required
- (void)close __attribute__((swift_name("close()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("GlobalDispatcher")))
@interface VMCGlobalDispatcher : VMCBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)globalDispatcher __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) VMCGlobalDispatcher *shared __attribute__((swift_name("shared")));
- (id<VMCKotlinCoroutineContext>)main __attribute__((swift_name("main()")));
@end

__attribute__((swift_name("ViewModelReporter")))
@protocol VMCViewModelReporter
@required
- (void)trackActionScreen:(NSString *)screen parent:(NSString *)parent noun:(NSString *)noun verb:(NSString *)verb success:(BOOL)success duration:(VMCLong * _Nullable)duration error:(VMCKotlinThrowable * _Nullable)error __attribute__((swift_name("trackAction(screen:parent:noun:verb:success:duration:error:)")));
- (void)trackNavigationScreen:(NSString *)screen __attribute__((swift_name("trackNavigation(screen:)")));
@end

__attribute__((unavailable("can't be imported")))
__attribute__((swift_name("AdaptedImmutableList")))
@interface VMCAdaptedImmutableList : NSObject
@end

__attribute__((swift_name("Change")))
@interface VMCChange : VMCBase
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Change.Deleted")))
@interface VMCChangeDeleted : VMCChange
- (instancetype)initWithFrom:(VMCKotlinIntRange *)from __attribute__((swift_name("init(from:)"))) __attribute__((objc_designated_initializer));
- (VMCChangeDeleted *)doCopyFrom:(VMCKotlinIntRange *)from __attribute__((swift_name("doCopy(from:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) VMCKotlinIntRange *from __attribute__((swift_name("from")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Change.Inserted")))
@interface VMCChangeInserted : VMCChange
- (instancetype)initWithTo:(VMCKotlinIntRange *)to __attribute__((swift_name("init(to:)"))) __attribute__((objc_designated_initializer));
- (VMCChangeInserted *)doCopyTo:(VMCKotlinIntRange *)to __attribute__((swift_name("doCopy(to:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) VMCKotlinIntRange *to __attribute__((swift_name("to")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Change.Moved")))
@interface VMCChangeMoved : VMCChange
- (instancetype)initWithFrom:(int32_t)from to:(int32_t)to __attribute__((swift_name("init(from:to:)"))) __attribute__((objc_designated_initializer));
- (VMCChangeMoved *)doCopyFrom:(int32_t)from to:(int32_t)to __attribute__((swift_name("doCopy(from:to:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) int32_t from __attribute__((swift_name("from")));
@property (readonly) int32_t to __attribute__((swift_name("to")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Change.Refresh")))
@interface VMCChangeRefresh : VMCChange
- (instancetype)initWithIndex:(int32_t)index __attribute__((swift_name("init(index:)"))) __attribute__((objc_designated_initializer));
- (VMCChangeRefresh *)doCopyIndex:(int32_t)index __attribute__((swift_name("doCopy(index:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) int32_t index __attribute__((swift_name("index")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Change.Reloaded")))
@interface VMCChangeReloaded : VMCChange
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)reloaded __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) VMCChangeReloaded *shared __attribute__((swift_name("shared")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Delta")))
@interface VMCDelta<T> : VMCBase
- (instancetype)initWithItems:(NSArray<id> *)items change:(NSArray<VMCChange *> *)change __attribute__((swift_name("init(items:change:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) VMCDeltaCompanion *companion __attribute__((swift_name("companion")));
- (VMCDelta<T> *)doCopyItems:(NSArray<id> *)items change:(NSArray<VMCChange *> *)change __attribute__((swift_name("doCopy(items:change:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSArray<VMCChange *> *change __attribute__((swift_name("change")));
@property (readonly) NSArray<id> *items __attribute__((swift_name("items")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("DeltaCompanion")))
@interface VMCDeltaCompanion : VMCBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) VMCDeltaCompanion *shared __attribute__((swift_name("shared")));
- (VMCDelta<id> *)empty __attribute__((swift_name("empty()")));
- (VMCDelta<id> *)reloadedItems:(NSArray<id> *)items __attribute__((swift_name("reloaded(items:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("EmptyItemPlaceholder")))
@interface VMCEmptyItemPlaceholder : VMCBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("HeaderItemPlaceholder")))
@interface VMCHeaderItemPlaceholder : VMCBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
@end

__attribute__((unavailable("can't be imported")))
__attribute__((swift_name("LazyMapList")))
@interface VMCLazyMapList : NSObject
@end

__attribute__((unavailable("can't be imported")))
__attribute__((swift_name("LazyMapPreviousList")))
@interface VMCLazyMapPreviousList : NSObject
@end

__attribute__((unavailable("can't be imported")))
__attribute__((swift_name("LazyMapPreviousNextList")))
@interface VMCLazyMapPreviousNextList : NSObject
@end

__attribute__((swift_name("SimpleImmutableListAdapter")))
@protocol VMCSimpleImmutableListAdapter
@required
- (id _Nullable)getIndex:(int32_t)index __attribute__((swift_name("get(index:)")));
@property (readonly) int32_t size __attribute__((swift_name("size")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinByteArray")))
@interface VMCKotlinByteArray : VMCBase
+ (instancetype)arrayWithSize:(int32_t)size __attribute__((swift_name("init(size:)")));
+ (instancetype)arrayWithSize:(int32_t)size init:(VMCByte *(^)(VMCInt *))init __attribute__((swift_name("init(size:init:)")));
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (int8_t)getIndex:(int32_t)index __attribute__((swift_name("get(index:)")));
- (VMCKotlinByteIterator *)iterator __attribute__((swift_name("iterator()")));
- (void)setIndex:(int32_t)index value:(int8_t)value __attribute__((swift_name("set(index:value:)")));
@property (readonly) int32_t size __attribute__((swift_name("size")));
@end

@interface VMCKotlinByteArray (Extensions)
- (NSString *)toHexString __attribute__((swift_name("toHexString()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Base64Kt")))
@interface VMCBase64Kt : VMCBase
+ (VMCKotlinByteArray *)dummyByteArrayMethodData:(VMCKotlinByteArray *)data __attribute__((swift_name("dummyByteArrayMethod(data:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FlowListKt")))
@interface VMCFlowListKt : VMCBase
+ (id<VMCKotlinx_coroutines_coreFlow>)combine:(NSArray<id<VMCKotlinx_coroutines_coreFlow>> *)receiver __attribute__((swift_name("combine(_:)")));
+ (id<VMCKotlinx_coroutines_coreFlow>)diffFlowList:(id<VMCKotlinx_coroutines_coreFlow>)receiver __attribute__((swift_name("diffFlowList(_:)")));
+ (id<VMCKotlinx_coroutines_coreFlow>)flowAsSingletonListFlow:(id<VMCKotlinx_coroutines_coreFlow>)flow predicate:(VMCBoolean *(^)(id _Nullable))predicate __attribute__((swift_name("flowAsSingletonList(flow:predicate:)")));
+ (id<VMCKotlinx_coroutines_coreFlow>)flowListOfItems:(VMCKotlinArray<id> *)items __attribute__((swift_name("flowListOf(items:)")));
+ (id<VMCKotlinx_coroutines_coreFlow>)flowListOfCallback:(id<VMCKotlinSuspendFunction0>)callback __attribute__((swift_name("flowListOf(callback:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FlowListConcatKt")))
@interface VMCFlowListConcatKt : VMCBase
+ (id<VMCKotlinx_coroutines_coreFlow>)concat:(id<VMCKotlinx_coroutines_coreFlow>)receiver other:(id<VMCKotlinx_coroutines_coreFlow>)other __attribute__((swift_name("concat(_:other:)")));
+ (id<VMCKotlinx_coroutines_coreFlow>)header:(id<VMCKotlinx_coroutines_coreFlow>)receiver header:(id)header __attribute__((swift_name("header(_:header:)")));
+ (id<VMCKotlinx_coroutines_coreFlow>)headerIfTrue:(id<VMCKotlinx_coroutines_coreFlow>)receiver header:(id)header condition:(id<VMCKotlinx_coroutines_coreFlow>)condition __attribute__((swift_name("headerIfTrue(_:header:condition:)")));
+ (id<VMCKotlinx_coroutines_coreFlow>)orEmpty:(id<VMCKotlinx_coroutines_coreFlow>)receiver emptyItem:(id)emptyItem __attribute__((swift_name("orEmpty(_:emptyItem:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FlowListMapKt")))
@interface VMCFlowListMapKt : VMCBase
+ (id<VMCKotlinx_coroutines_coreFlow>)flowLazyMap:(id<VMCKotlinx_coroutines_coreFlow>)receiver transform:(id _Nullable (^)(id _Nullable))transform __attribute__((swift_name("flowLazyMap(_:transform:)")));
+ (NSArray<id> *)lazyMap:(NSArray<id> *)receiver transform:(id _Nullable (^)(id _Nullable))transform __attribute__((swift_name("lazyMap(_:transform:)")));
+ (id<VMCKotlinx_coroutines_coreFlow>)lazyMap:(id<VMCKotlinx_coroutines_coreFlow>)receiver transform_:(id _Nullable (^)(id _Nullable))transform __attribute__((swift_name("lazyMap(_:transform_:)")));
+ (id<VMCKotlinx_coroutines_coreFlow>)lazyMapPrevious:(id<VMCKotlinx_coroutines_coreFlow>)receiver transform:(id _Nullable (^)(id _Nullable, id _Nullable))transform __attribute__((swift_name("lazyMapPrevious(_:transform:)")));
+ (id<VMCKotlinx_coroutines_coreFlow>)lazyMapPreviousNext:(id<VMCKotlinx_coroutines_coreFlow>)receiver transform:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable))transform __attribute__((swift_name("lazyMapPreviousNext(_:transform:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FlowToolsKt")))
@interface VMCFlowToolsKt : VMCBase
+ (id<VMCKotlinx_coroutines_coreFlow>)viewModelAsSingletonListViewModel:(id<VMCViewModel>)viewModel predicate:(VMCBoolean *(^)(id _Nullable))predicate __attribute__((swift_name("viewModelAsSingletonList(viewModel:predicate:)")));
+ (id<VMCFlowCloseable>)watchFlowFlow:(id<VMCKotlinx_coroutines_coreFlow>)flow block:(void (^)(id _Nullable))block __attribute__((swift_name("watchFlow(flow:block:)")));
+ (VMCCFlow<id> *)wrap:(id<VMCKotlinx_coroutines_coreFlow>)receiver __attribute__((swift_name("wrap(_:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("HexKt")))
@interface VMCHexKt : VMCBase
+ (VMCKotlinByteArray *)fromHexString:(NSString *)receiver __attribute__((swift_name("fromHexString(_:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("IosToolsKt")))
@interface VMCIosToolsKt : VMCBase
+ (NSData *)byteArrayToDataBytes:(VMCKotlinByteArray *)bytes __attribute__((swift_name("byteArrayToData(bytes:)")));
@end

__attribute__((swift_name("KotlinKAnnotatedElement")))
@protocol VMCKotlinKAnnotatedElement
@required
@end

__attribute__((swift_name("KotlinKCallable")))
@protocol VMCKotlinKCallable <VMCKotlinKAnnotatedElement>
@required
@property (readonly) NSString *name __attribute__((swift_name("name")));
@property (readonly) id<VMCKotlinKType> returnType __attribute__((swift_name("returnType")));
@end

__attribute__((swift_name("KotlinKProperty")))
@protocol VMCKotlinKProperty <VMCKotlinKCallable>
@required
@end

__attribute__((swift_name("KotlinThrowable")))
@interface VMCKotlinThrowable : VMCBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(VMCKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(VMCKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));

/**
 * @note annotations
 *   kotlin.experimental.ExperimentalNativeApi
*/
- (VMCKotlinArray<NSString *> *)getStackTrace __attribute__((swift_name("getStackTrace()")));
- (void)printStackTrace __attribute__((swift_name("printStackTrace()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) VMCKotlinThrowable * _Nullable cause __attribute__((swift_name("cause")));
@property (readonly) NSString * _Nullable message __attribute__((swift_name("message")));
- (NSError *)asError __attribute__((swift_name("asError()")));
@end

__attribute__((swift_name("KotlinException")))
@interface VMCKotlinException : VMCKotlinThrowable
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(VMCKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(VMCKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end

__attribute__((swift_name("KotlinRuntimeException")))
@interface VMCKotlinRuntimeException : VMCKotlinException
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(VMCKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(VMCKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end

__attribute__((swift_name("KotlinIllegalStateException")))
@interface VMCKotlinIllegalStateException : VMCKotlinRuntimeException
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(VMCKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(VMCKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.4")
*/
__attribute__((swift_name("KotlinCancellationException")))
@interface VMCKotlinCancellationException : VMCKotlinIllegalStateException
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(VMCKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(VMCKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end

__attribute__((swift_name("KotlinFunction")))
@protocol VMCKotlinFunction
@required
@end

__attribute__((swift_name("KotlinSuspendFunction1")))
@protocol VMCKotlinSuspendFunction1 <VMCKotlinFunction>
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)invokeP1:(id _Nullable)p1 completionHandler:(void (^)(id _Nullable_result, NSError * _Nullable))completionHandler __attribute__((swift_name("invoke(p1:completionHandler:)")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.3")
*/
__attribute__((swift_name("KotlinCoroutineContext")))
@protocol VMCKotlinCoroutineContext
@required
- (id _Nullable)foldInitial:(id _Nullable)initial operation:(id _Nullable (^)(id _Nullable, id<VMCKotlinCoroutineContextElement>))operation __attribute__((swift_name("fold(initial:operation:)")));
- (id<VMCKotlinCoroutineContextElement> _Nullable)getKey:(id<VMCKotlinCoroutineContextKey>)key __attribute__((swift_name("get(key:)")));
- (id<VMCKotlinCoroutineContext>)minusKeyKey:(id<VMCKotlinCoroutineContextKey>)key __attribute__((swift_name("minusKey(key:)")));
- (id<VMCKotlinCoroutineContext>)plusContext:(id<VMCKotlinCoroutineContext>)context __attribute__((swift_name("plus(context:)")));
@end

__attribute__((swift_name("KotlinCoroutineContextElement")))
@protocol VMCKotlinCoroutineContextElement <VMCKotlinCoroutineContext>
@required
@property (readonly) id<VMCKotlinCoroutineContextKey> key __attribute__((swift_name("key")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreJob")))
@protocol VMCKotlinx_coroutines_coreJob <VMCKotlinCoroutineContextElement>
@required
- (id<VMCKotlinx_coroutines_coreChildHandle>)attachChildChild:(id<VMCKotlinx_coroutines_coreChildJob>)child __attribute__((swift_name("attachChild(child:)")));
- (void)cancelCause:(VMCKotlinCancellationException * _Nullable)cause __attribute__((swift_name("cancel(cause:)")));
- (VMCKotlinCancellationException *)getCancellationException __attribute__((swift_name("getCancellationException()")));
- (id<VMCKotlinx_coroutines_coreDisposableHandle>)invokeOnCompletionHandler:(void (^)(VMCKotlinThrowable * _Nullable))handler __attribute__((swift_name("invokeOnCompletion(handler:)")));
- (id<VMCKotlinx_coroutines_coreDisposableHandle>)invokeOnCompletionOnCancelling:(BOOL)onCancelling invokeImmediately:(BOOL)invokeImmediately handler:(void (^)(VMCKotlinThrowable * _Nullable))handler __attribute__((swift_name("invokeOnCompletion(onCancelling:invokeImmediately:handler:)")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)joinWithCompletionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("join(completionHandler:)")));
- (id<VMCKotlinx_coroutines_coreJob>)plusOther:(id<VMCKotlinx_coroutines_coreJob>)other __attribute__((swift_name("plus(other:)"))) __attribute__((unavailable("Operator '+' on two Job objects is meaningless. Job is a coroutine context element and `+` is a set-sum operator for coroutine contexts. The job to the right of `+` just replaces the job the left of `+`.")));
- (BOOL)start __attribute__((swift_name("start()")));
@property (readonly) id<VMCKotlinSequence> children __attribute__((swift_name("children")));
@property (readonly) BOOL isActive __attribute__((swift_name("isActive")));
@property (readonly) BOOL isCancelled __attribute__((swift_name("isCancelled")));
@property (readonly) BOOL isCompleted __attribute__((swift_name("isCompleted")));
@property (readonly) id<VMCKotlinx_coroutines_coreSelectClause0> onJoin __attribute__((swift_name("onJoin")));

/**
 * @note annotations
 *   kotlinx.coroutines.ExperimentalCoroutinesApi
*/
@property (readonly) id<VMCKotlinx_coroutines_coreJob> _Nullable parent __attribute__((swift_name("parent")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreCompletableJob")))
@protocol VMCKotlinx_coroutines_coreCompletableJob <VMCKotlinx_coroutines_coreJob>
@required
- (BOOL)complete __attribute__((swift_name("complete()")));
- (BOOL)completeExceptionallyException:(VMCKotlinThrowable *)exception __attribute__((swift_name("completeExceptionally(exception:)")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreFlowCollector")))
@protocol VMCKotlinx_coroutines_coreFlowCollector
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)emitValue:(id _Nullable)value completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("emit(value:completionHandler:)")));
@end

__attribute__((swift_name("KotlinIterable")))
@protocol VMCKotlinIterable
@required
- (id<VMCKotlinIterator>)iterator __attribute__((swift_name("iterator()")));
@end

__attribute__((swift_name("KotlinIntProgression")))
@interface VMCKotlinIntProgression : VMCBase <VMCKotlinIterable>
@property (class, readonly, getter=companion) VMCKotlinIntProgressionCompanion *companion __attribute__((swift_name("companion")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (BOOL)isEmpty __attribute__((swift_name("isEmpty()")));
- (VMCKotlinIntIterator *)iterator __attribute__((swift_name("iterator()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) int32_t first __attribute__((swift_name("first")));
@property (readonly) int32_t last __attribute__((swift_name("last")));
@property (readonly) int32_t step __attribute__((swift_name("step")));
@end

__attribute__((swift_name("KotlinClosedRange")))
@protocol VMCKotlinClosedRange
@required
- (BOOL)containsValue:(id)value __attribute__((swift_name("contains(value:)")));
- (BOOL)isEmpty __attribute__((swift_name("isEmpty()")));
@property (readonly) id endInclusive __attribute__((swift_name("endInclusive")));
@property (readonly, getter=start_) id start __attribute__((swift_name("start")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.9")
*/
__attribute__((swift_name("KotlinOpenEndRange")))
@protocol VMCKotlinOpenEndRange
@required
- (BOOL)containsValue_:(id)value __attribute__((swift_name("contains(value_:)")));
- (BOOL)isEmpty __attribute__((swift_name("isEmpty()")));
@property (readonly) id endExclusive __attribute__((swift_name("endExclusive")));
@property (readonly, getter=start_) id start __attribute__((swift_name("start")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinIntRange")))
@interface VMCKotlinIntRange : VMCKotlinIntProgression <VMCKotlinClosedRange, VMCKotlinOpenEndRange>
- (instancetype)initWithStart:(int32_t)start endInclusive:(int32_t)endInclusive __attribute__((swift_name("init(start:endInclusive:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) VMCKotlinIntRangeCompanion *companion __attribute__((swift_name("companion")));
- (BOOL)containsValue:(VMCInt *)value __attribute__((swift_name("contains(value:)")));
- (BOOL)containsValue_:(VMCInt *)value __attribute__((swift_name("contains(value_:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (BOOL)isEmpty __attribute__((swift_name("isEmpty()")));
- (NSString *)description __attribute__((swift_name("description()")));

/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.9")
*/
@property (readonly) VMCInt *endExclusive __attribute__((swift_name("endExclusive"))) __attribute__((deprecated("Can throw an exception when it's impossible to represent the value with Int type, for example, when the range includes MAX_VALUE. It's recommended to use 'endInclusive' property that doesn't throw.")));
@property (readonly) VMCInt *endInclusive __attribute__((swift_name("endInclusive")));
@property (readonly, getter=start_) VMCInt *start __attribute__((swift_name("start")));
@end

__attribute__((swift_name("KotlinIterator")))
@protocol VMCKotlinIterator
@required
- (BOOL)hasNext __attribute__((swift_name("hasNext()")));
- (id _Nullable)next __attribute__((swift_name("next()")));
@end

__attribute__((swift_name("KotlinByteIterator")))
@interface VMCKotlinByteIterator : VMCBase <VMCKotlinIterator>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (VMCByte *)next __attribute__((swift_name("next()")));
- (int8_t)nextByte __attribute__((swift_name("nextByte()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinArray")))
@interface VMCKotlinArray<T> : VMCBase
+ (instancetype)arrayWithSize:(int32_t)size init:(T _Nullable (^)(VMCInt *))init __attribute__((swift_name("init(size:init:)")));
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (T _Nullable)getIndex:(int32_t)index __attribute__((swift_name("get(index:)")));
- (id<VMCKotlinIterator>)iterator __attribute__((swift_name("iterator()")));
- (void)setIndex:(int32_t)index value:(T _Nullable)value __attribute__((swift_name("set(index:value:)")));
@property (readonly) int32_t size __attribute__((swift_name("size")));
@end

__attribute__((swift_name("KotlinSuspendFunction0")))
@protocol VMCKotlinSuspendFunction0 <VMCKotlinFunction>
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)invokeWithCompletionHandler:(void (^)(id _Nullable_result, NSError * _Nullable))completionHandler __attribute__((swift_name("invoke(completionHandler:)")));
@end

__attribute__((swift_name("KotlinKType")))
@protocol VMCKotlinKType
@required

/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.1")
*/
@property (readonly) NSArray<VMCKotlinKTypeProjection *> *arguments __attribute__((swift_name("arguments")));

/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.1")
*/
@property (readonly) id<VMCKotlinKClassifier> _Nullable classifier __attribute__((swift_name("classifier")));
@property (readonly) BOOL isMarkedNullable __attribute__((swift_name("isMarkedNullable")));
@end

__attribute__((swift_name("KotlinCoroutineContextKey")))
@protocol VMCKotlinCoroutineContextKey
@required
@end

__attribute__((swift_name("Kotlinx_coroutines_coreDisposableHandle")))
@protocol VMCKotlinx_coroutines_coreDisposableHandle
@required
- (void)dispose __attribute__((swift_name("dispose()")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreChildHandle")))
@protocol VMCKotlinx_coroutines_coreChildHandle <VMCKotlinx_coroutines_coreDisposableHandle>
@required
- (BOOL)childCancelledCause:(VMCKotlinThrowable *)cause __attribute__((swift_name("childCancelled(cause:)")));
@property (readonly) id<VMCKotlinx_coroutines_coreJob> _Nullable parent __attribute__((swift_name("parent")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreChildJob")))
@protocol VMCKotlinx_coroutines_coreChildJob <VMCKotlinx_coroutines_coreJob>
@required
- (void)parentCancelledParentJob:(id<VMCKotlinx_coroutines_coreParentJob>)parentJob __attribute__((swift_name("parentCancelled(parentJob:)")));
@end

__attribute__((swift_name("KotlinSequence")))
@protocol VMCKotlinSequence
@required
- (id<VMCKotlinIterator>)iterator __attribute__((swift_name("iterator()")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreSelectClause")))
@protocol VMCKotlinx_coroutines_coreSelectClause
@required
@property (readonly) id clauseObject __attribute__((swift_name("clauseObject")));
@property (readonly) VMCKotlinUnit *(^(^ _Nullable onCancellationConstructor)(id<VMCKotlinx_coroutines_coreSelectInstance>, id _Nullable, id _Nullable))(VMCKotlinThrowable *) __attribute__((swift_name("onCancellationConstructor")));
@property (readonly) id _Nullable (^processResFunc)(id, id _Nullable, id _Nullable) __attribute__((swift_name("processResFunc")));
@property (readonly) void (^regFunc)(id, id<VMCKotlinx_coroutines_coreSelectInstance>, id _Nullable) __attribute__((swift_name("regFunc")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreSelectClause0")))
@protocol VMCKotlinx_coroutines_coreSelectClause0 <VMCKotlinx_coroutines_coreSelectClause>
@required
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinIntProgression.Companion")))
@interface VMCKotlinIntProgressionCompanion : VMCBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) VMCKotlinIntProgressionCompanion *shared __attribute__((swift_name("shared")));
- (VMCKotlinIntProgression *)fromClosedRangeRangeStart:(int32_t)rangeStart rangeEnd:(int32_t)rangeEnd step:(int32_t)step __attribute__((swift_name("fromClosedRange(rangeStart:rangeEnd:step:)")));
@end

__attribute__((swift_name("KotlinIntIterator")))
@interface VMCKotlinIntIterator : VMCBase <VMCKotlinIterator>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (VMCInt *)next __attribute__((swift_name("next()")));
- (int32_t)nextInt __attribute__((swift_name("nextInt()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinIntRange.Companion")))
@interface VMCKotlinIntRangeCompanion : VMCBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) VMCKotlinIntRangeCompanion *shared __attribute__((swift_name("shared")));
@property (readonly) VMCKotlinIntRange *EMPTY __attribute__((swift_name("EMPTY")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.1")
*/
__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinKTypeProjection")))
@interface VMCKotlinKTypeProjection : VMCBase
- (instancetype)initWithVariance:(VMCKotlinKVariance * _Nullable)variance type:(id<VMCKotlinKType> _Nullable)type __attribute__((swift_name("init(variance:type:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) VMCKotlinKTypeProjectionCompanion *companion __attribute__((swift_name("companion")));
- (VMCKotlinKTypeProjection *)doCopyVariance:(VMCKotlinKVariance * _Nullable)variance type:(id<VMCKotlinKType> _Nullable)type __attribute__((swift_name("doCopy(variance:type:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) id<VMCKotlinKType> _Nullable type __attribute__((swift_name("type")));
@property (readonly) VMCKotlinKVariance * _Nullable variance __attribute__((swift_name("variance")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.1")
*/
__attribute__((swift_name("KotlinKClassifier")))
@protocol VMCKotlinKClassifier
@required
@end

__attribute__((swift_name("Kotlinx_coroutines_coreParentJob")))
@protocol VMCKotlinx_coroutines_coreParentJob <VMCKotlinx_coroutines_coreJob>
@required
- (VMCKotlinCancellationException *)getChildJobCancellationCause __attribute__((swift_name("getChildJobCancellationCause()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinUnit")))
@interface VMCKotlinUnit : VMCBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)unit __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) VMCKotlinUnit *shared __attribute__((swift_name("shared")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreSelectInstance")))
@protocol VMCKotlinx_coroutines_coreSelectInstance
@required
- (void)disposeOnCompletionDisposableHandle:(id<VMCKotlinx_coroutines_coreDisposableHandle>)disposableHandle __attribute__((swift_name("disposeOnCompletion(disposableHandle:)")));
- (void)selectInRegistrationPhaseInternalResult:(id _Nullable)internalResult __attribute__((swift_name("selectInRegistrationPhase(internalResult:)")));
- (BOOL)trySelectClauseObject:(id)clauseObject result:(id _Nullable)result __attribute__((swift_name("trySelect(clauseObject:result:)")));
@property (readonly) id<VMCKotlinCoroutineContext> context __attribute__((swift_name("context")));
@end

__attribute__((swift_name("KotlinComparable")))
@protocol VMCKotlinComparable
@required
- (int32_t)compareToOther:(id _Nullable)other __attribute__((swift_name("compareTo(other:)")));
@end

__attribute__((swift_name("KotlinEnum")))
@interface VMCKotlinEnum<E> : VMCBase <VMCKotlinComparable>
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) VMCKotlinEnumCompanion *companion __attribute__((swift_name("companion")));
- (int32_t)compareToOther:(E)other __attribute__((swift_name("compareTo(other:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@property (readonly) int32_t ordinal __attribute__((swift_name("ordinal")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.1")
*/
__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinKVariance")))
@interface VMCKotlinKVariance : VMCKotlinEnum<VMCKotlinKVariance *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) VMCKotlinKVariance *invariant __attribute__((swift_name("invariant")));
@property (class, readonly) VMCKotlinKVariance *in __attribute__((swift_name("in")));
@property (class, readonly) VMCKotlinKVariance *out __attribute__((swift_name("out")));
+ (VMCKotlinArray<VMCKotlinKVariance *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<VMCKotlinKVariance *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinKTypeProjection.Companion")))
@interface VMCKotlinKTypeProjectionCompanion : VMCBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) VMCKotlinKTypeProjectionCompanion *shared __attribute__((swift_name("shared")));

/**
 * @note annotations
 *   kotlin.jvm.JvmStatic
*/
- (VMCKotlinKTypeProjection *)contravariantType:(id<VMCKotlinKType>)type __attribute__((swift_name("contravariant(type:)")));

/**
 * @note annotations
 *   kotlin.jvm.JvmStatic
*/
- (VMCKotlinKTypeProjection *)covariantType:(id<VMCKotlinKType>)type __attribute__((swift_name("covariant(type:)")));

/**
 * @note annotations
 *   kotlin.jvm.JvmStatic
*/
- (VMCKotlinKTypeProjection *)invariantType:(id<VMCKotlinKType>)type __attribute__((swift_name("invariant(type:)")));
@property (readonly) VMCKotlinKTypeProjection *STAR __attribute__((swift_name("STAR")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinEnumCompanion")))
@interface VMCKotlinEnumCompanion : VMCBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) VMCKotlinEnumCompanion *shared __attribute__((swift_name("shared")));
@end

#pragma pop_macro("_Nullable_result")
#pragma clang diagnostic pop
NS_ASSUME_NONNULL_END

package nim.shs1330.netease.com.tasksys;

/**
 * Created by shs1330 on 2017/10/20.
 * DroidPlugin 使用的是激进的类管理：每个插件都有一个{@link java.lang.ClassLoader}
 * Small 使用的是保守的类管理，委托系统帮我们加载Class
 * <p>
 * 2017年10月23日17:39:43
 * 对于广播的注册，插件中的静态广播也作为动态广播注册，动态添加IntentFilter
 * <p>
 * 2017年10月25日10:51:07
 * 对于借尸还魂这种hookActivity的方式是有侵入的，我们需要修改大量的AMS，PMS，ActivityThread里面的变量。
 * <p>
 * 对于DL的方式，其将host中的ProxyActivity（插件Activity的host代理）传入到plugin中然后在plugin中对这个ProxyActivity进行
 * 操作，展示给用户，这种方式是无侵入的
 * <p>
 * 2017年10月27日09:34:03
 * 无侵入的插件化其实还是调用的宿主的一些方法，使用宿主的Context，所以插件中无法使用R开头访问资源
 * 有侵入事实上就是插件自己的App的Context所以可以访问资源，
 * <p>
 * 有侵入的plugin为什么能访问资源呢？
 * 访问资源和Resource这个类有关，和Context的getAssets()方法有关，ContextImpl是的实现类，我们在ActivityThread
 * 中看到在createContext时候自动生成Resource，生成它是根据packageInfo（LoadedApk）生成的所以。。。，而LoadedApk
 * 是我们createContext传给他的对应插件的apk
 * <p>
 * 很多坑，
 * 2017年10月27日16:40:09
 * 插件里的application要首先启动一下再生产，所以直接调用插件中的fragment不能加载插件中的资源，于是我们自己new application然后
 * 导入到loadedApk中（ApplicationInfo）
 * <p>
 * 2017年10月30日13:04:48
 * 无侵入的DL框架
 * 通过引入一个中间层，
 * Host和Plugin全部都依赖于这个lib Interface，
 * 当host的intent进行startActivity的时候，注入其他extra的信息，在proxyActivity的onCreate方法里面接受到该intent，
 * 随后引入DLManager处理，在此manager中处理插件activity的启动数据注入等。
 * <p>
 * 2017年10月31日14:06:27
 * 在插件中用host的context注册广播就可正常接收信息
 * <p>
 * 2017年11月1日10:46:03
 * Activity和Service有很多相同点也有很多不同点
 * 他们都是四大组件，都通过AMS管理生命周期，但是Activity的生命周期由用户触发，由系统管理，Service的生命周期一般由我们代码控制，
 * 除非内存吃紧。
 * Service的实例在start方式下只调用一次，而且Service有可能会在一个新的进程中，这导致Activity和Servicehook机制不能通用的原因
 * 具体我们自己保存一个map来模仿startService，我们在AndroidManifest里面生命五个不同进程的Service和一个ProxyService，所有Service
 * 的启动通过Service完成
 * <p>
 *
 * 2017年11月3日13:21:30
 * BroadcastReceiver IIntentReceiver 注入到AMS，Intent的匹配也是在AMS中，然后回调IntentReceiver的方法，发送至handler进行调用
 * Toast TN 注入到INotificationManager， 经过验证如Context的验证等，通过TN回调本地WindowsManager
 * bindService 是{@link android.content.ContextWrapper}的方法，这是个装饰者模式，里面的方法直接调用我们attach的Context
 * 在这里面将我们的ServiceConnection封装成一个IServiceConnection（Binder），交给AMS，AMS经过一系列人验证通过app.thread
 * IPC至本地进程完成Service的创建。这是如果Service不再同一个进程先fork一个进程在进行创建，之后再app.thread里面IPC通知AMS
 * Service已经创建完成，然后AMS再次通过IPC app.thread进行bind，这时调用Service的onBind方法并将此binder发送給AMS，AMS通过IPC
 * 调用IServiceConnection，并将IBinder对象发送给本地，这样本地和远程服务就建立了联系。
 * <p>
 * 2017年11月6日10:46:51
 * ContentProvider的获取和IContentProvider
 * AMS的installProvider本地和远程执行不同的代码，远程执行newInstance，holder和provider都为空，之后通过AMS的publish方法返回给本地进程，holder和
 * provider都不为空，直接保存到本地
 * <p>
 * 2017年11月7日09:31:33
 * 在Activity中调用{@link android.app.Activity#getContentResolver()}方法事实上是是调用ContextImpl的方法，获取到ContentResolver
 * ContentResolver是一个抽象类，他在ContextImpl的具体实现是ApplicationContentResolver，我们获取ContentProvider最终会调用它的
 * acquireProvider方法，这个方法又会调用ActivityThread的acquireProvider方法。
 * 在ActivityThread中首先查找本地进程是否已经存在该ContentProvider，如果已经存在这直接返回，否则通过ActivityManagerService进行
 * 远程唤醒。调用AMS的getContentProvider方法，这个方法中先判断是否有缓存，如果没有在进行远程唤醒。
 * 如果没有唤醒判断该进程是否已经唤醒getProcessRecordLocked如果不为空则通过ApplicationThread的scheduleInstallProvider进行远程安装，
 * 然后调用ActivityThread的installContentProviders方法随后调用installProvider方法完成安装，最后调用AMS的publishContentProviders
 * 将此ContentProvider进行发布。
 * 如果该进程没有被唤醒则即getProcessRecordLocked方法返回空，则调用startProcessLocked启动一个进程，通过ActivityThread#main方法
 * ActivityThread#attach方法，AMS#attachApplication方法在此方法会回调ApplicationThread的bindApplication方法，这个方法通过H
 * (handler)调用ActivityThread的handleBindApplication方法，随后调用installContentProviders完成安装，然后进行发布
 * <p>
 * 2017年11月8日09:30:51
 * contentProvider使用的时候懒加载，只用使用的时候才会被加载，平常非AMS拉起来的进程他的provider为空所以在执行handleBindApplication
 * 的时候没有要install的provider，因为现在进程已经启动调用他的scheduleInstallProvider方法完成本地安装，
 * 而未启动的时候，我将要启动的provider封装到一个Intent里面，在handleBindApplication里面检测到非空，进行
 * 安装
 *
 * 2017年11月9日13:04:04
 * Binder Driver
 *
 * 2017年11月13日10:52:56
 * SystemService.java 完成对AMS的初始化，并通过ServiceManager.addService添加到SM中
 * 每一个进程都会调用其main方法，创建ApplicationThread吗，每个进程都有主线程
 *
 * 新版的StartService方法创建进程及之后回调创建Service，或者直接在本进程创建Service的相关逻辑放在ActiveServices * 这个类里面
 *
 *
 * 2017年11月15日09:31:11 main启动过程
 * app启动的时候先执行main方法，完成MainLooper的初始化，完成mainHandler的初始化，new出来ActivityThread
 * ，接着调用它的attach方法，将本地进程的入口——ApplicationThread注册进AMS，这样其他进程就可以通过AMS里面的
 * ApplicationThread找到我们，随后在AMS中第一步先调用bindApplication方法完成本地进程的Instrumentation的初始化
 * 完成Application的初始化，installContentProvider，随后AMS里面检查是否有要启动的Activity，通过ActivityStackSupervisor完成realStartActivity，
 * 检查是否有要启动的Service，通过ActiveServices的realStartService创建Service
 *
 * 2017年11月15日09:59:21 Application
 * 主线程中的application对象是在handleBindApplication方法中通过LoadedApk的方法创建，LoadedApk对象唯一。
 * 插件中的Application对象显然不是在handleBindApplication方法生成，是在第一个Activity被生成之后，生成的，之后
 * 调用它的onCreate方法，但是也是通过LoadedApk生成的，故只能生成一次。
 */
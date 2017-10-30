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
 */
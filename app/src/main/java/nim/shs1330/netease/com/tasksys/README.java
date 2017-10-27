package nim.shs1330.netease.com.tasksys;

/**
 * Created by shs1330 on 2017/10/20.
 * DroidPlugin 使用的是激进的类管理：每个插件都有一个{@link java.lang.ClassLoader}
 * Small 使用的是保守的类管理，委托系统帮我们加载Class
 */

/**
 * 2017年10月23日17:39:43
 * 对于广播的注册，插件中的静态广播也作为动态广播注册，动态添加IntentFilter
 */

/**
 * 2017年10月25日10:51:07
 * 对于借尸还魂这种hookActivity的方式是有侵入的，我们需要修改大量的AMS，PMS，ActivityThread里面的变量。
 *
 * 对于DL的方式，其将host中的ProxyActivity（插件Activity的host代理）传入到plugin中然后在plugin中对这个ProxyActivity进行
 * 操作，展示给用户，这种方式是无侵入的
 */

/**
 * 2017年10月27日09:34:03
 * 无侵入的插件化其实还是调用的宿主的一些方法，使用宿主的Context，所以插件中无法使用R开头访问资源
 * 有侵入事实上就是插件自己的App的Context所以可以访问资源
 */
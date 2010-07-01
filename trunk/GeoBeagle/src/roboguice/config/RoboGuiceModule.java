package roboguice.config;

import java.util.List;

import roboguice.application.GuiceApplication;
import roboguice.inject.ActivityProvider;
import roboguice.inject.ContentResolverProvider;
import roboguice.inject.ContextScope;
import roboguice.inject.ContextScoped;
import roboguice.inject.ExtrasListener;
import roboguice.inject.ResourceListener;
import roboguice.inject.ResourcesProvider;
import roboguice.inject.SharedPreferencesProvider;
import roboguice.inject.SystemServiceProvider;
import roboguice.inject.ViewListener;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.matcher.Matchers;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * A Module that provides bindings and configuration to use Guice on Android.
 * Used by {@link GuiceApplication}.
 * 
 * @author Mike Burton
 * @author Pierre-Yves Ricau (py.ricau+roboguice@gmail.com)
 */
public class RoboGuiceModule extends AbstractModule {

    protected final ContextScope contextScope;
    protected final Provider<Context> throwingContextProvider;
    protected final Provider<Context> contextProvider;
    protected final ResourceListener resourceListener;
    protected final ViewListener viewListener;
    protected final ExtrasListener extrasListener;
    protected final Application application;

    public RoboGuiceModule(ContextScope contextScope, Provider<Context> throwingContextProvider, Provider<Context> contextProvider,
            ResourceListener resourceListener, ViewListener viewListener, ExtrasListener extrasListener, Application application) {
        this.contextScope = contextScope;
        this.throwingContextProvider = throwingContextProvider;
        this.contextProvider = contextProvider;
        this.resourceListener = resourceListener;
        this.viewListener = viewListener;
        this.extrasListener = extrasListener;
        this.application = application;
    }

    /**
     * Configure this module to define Android related bindings.<br />
     * <br />
     * If you want to provide your own bindings, you should <strong>NOT</strong>
     * override this method, but rather create a {@link Module} implementation
     * and add it to the configuring modules by overriding
     * {@link GuiceApplication#addApplicationModules(List)}.<br />
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void configure() {
        // Sundry Android Classes
//        bind(SharedPreferences.class).toProvider(SharedPreferencesProvider.class);
        bind(Resources.class).toProvider(ResourcesProvider.class);
        bind(ContentResolver.class).toProvider(ContentResolverProvider.class);

        for (Class<? extends Object> c = application.getClass(); c != null && Application.class.isAssignableFrom(c); c = c.getSuperclass()) {
            bind((Class<Object>) c).toInstance(application);
        }

        // System Services
        bind(LocationManager.class).toProvider(new SystemServiceProvider<LocationManager>(Context.LOCATION_SERVICE));
        bind(WindowManager.class).toProvider(new SystemServiceProvider<WindowManager>(Context.WINDOW_SERVICE));
        bind(LayoutInflater.class).toProvider(new SystemServiceProvider<LayoutInflater>(Context.LAYOUT_INFLATER_SERVICE));
        bind(ActivityManager.class).toProvider(new SystemServiceProvider<ActivityManager>(Context.ACTIVITY_SERVICE));
        bind(PowerManager.class).toProvider(new SystemServiceProvider<PowerManager>(Context.POWER_SERVICE));
//        bind(AlarmManager.class).toProvider(new SystemServiceProvider<AlarmManager>(Context.ALARM_SERVICE));
//        bind(NotificationManager.class).toProvider(new SystemServiceProvider<NotificationManager>(Context.NOTIFICATION_SERVICE));
//        bind(KeyguardManager.class).toProvider(new SystemServiceProvider<KeyguardManager>(Context.KEYGUARD_SERVICE));
//        bind(SearchManager.class).toProvider(new SystemServiceProvider<SearchManager>(Context.SEARCH_SERVICE));
//        bind(Vibrator.class).toProvider(new SystemServiceProvider<Vibrator>(Context.VIBRATOR_SERVICE));
//        bind(ConnectivityManager.class).toProvider(new SystemServiceProvider<ConnectivityManager>(Context.CONNECTIVITY_SERVICE));
        bind(WifiManager.class).toProvider(new SystemServiceProvider<WifiManager>(Context.WIFI_SERVICE));
//        bind(InputMethodManager.class).toProvider(new SystemServiceProvider<InputMethodManager>(Context.INPUT_METHOD_SERVICE));

        // Context Scope bindings
        bindScope(ContextScoped.class, contextScope);
        bind(ContextScope.class).toInstance(contextScope);
        bind(Context.class).toProvider(throwingContextProvider).in(ContextScoped.class);
        bind(Activity.class).toProvider(ActivityProvider.class);

        // Android Resources, Views and extras require special handling
        /*
        bindListener(Matchers.any(), resourceListener);
        bindListener(Matchers.any(), extrasListener);
        bindListener(Matchers.any(), viewListener);
        */
    }

}
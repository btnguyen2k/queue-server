package controllers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import play.api.templates.Html;
import play.i18n.Lang;
import play.mvc.Controller;

/**
 * Base class for other controllers.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class BaseController extends Controller {
    /**
     * Utility method to render a HTML page.
     * 
     * @param view
     * @param params
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    protected static Html render(String view, Object... params) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        String clazzName = "views.html." + view;
        Class<?> clazz = Class.forName(clazzName);

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals("render")) {
                Lang lang = lang();
                Object[] combinedParams = new Object[params.length + 1];
                combinedParams[params.length] = lang;
                for (int i = 0; i < params.length; i++) {
                    combinedParams[i] = params[i];
                }
                return (Html) method.invoke(null, combinedParams);
            }
        }
        return null;
    }
}

package com.money.Filter;

import com.money.annotation.Token;
import com.money.memcach.MemCachService;
import org.hibernate.Session;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by liumin on 15/12/18.
 */
public class TokenInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            Token annotation = method.getAnnotation(Token.class);
            if (annotation != null) {
                boolean needSaveSession = annotation.save();
                if (needSaveSession) {
                    String key = "UrlToken::"+request.getSession(true).getId();
                    String UUIDStr = UUID.randomUUID().toString().replaceAll("\\-", "");
                    MemCachService.MemCachSet( key,UUIDStr);
                    MemCachService.SetTimeOfKey( key,3600 );
                    response.setHeader( "UrlToken",UUIDStr );
                }
                boolean needRemoveSession = annotation.remove();
                if (needRemoveSession) {
                    if (isRepeatSubmit(request)) {
                        return false;
                    }

                    HttpSession session = request.getSession(false);
                    if( session != null ){
                        MemCachService.RemoveValue( "UrlToken::"+session.getId() );
                    }
                }
            }

            return true;
        } else {
            return super.preHandle(request, response, handler);
        }
    }

    private boolean isRepeatSubmit(HttpServletRequest request) {
        String serverToken = MemCachService.MemCachgGet( "UrlToken::"+request.getSession(false).getId() );
        if (serverToken == null || serverToken.equals("") ) {
            return true;
        }
        String clinetToken = request.getParameter("UrlToken");
        if (clinetToken == null) {
            return false;
        }
        if (!serverToken.equals(clinetToken)) {
            return true;
        }
        return false;
    }
}

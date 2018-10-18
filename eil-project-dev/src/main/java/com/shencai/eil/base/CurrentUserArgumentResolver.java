package com.shencai.eil.base;

import com.github.jsonzou.jmockdata.JMockData;
import com.shencai.eil.base.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import com.shencai.eil.common.utils.ObjectUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: eil-project
 * @description:
 * @author: fujialong
 * @create: 2018-09-12
 **/
@Slf4j
public class CurrentUserArgumentResolver implements WebArgumentResolver {
    @Override
    public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest nativeWebRequest) throws Exception {

        if (!ObjectUtil.isEmpty(methodParameter.getParameter()) &&
                methodParameter.getParameter().equals(UserInfo.class)) {
            HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
            String ticket = request.getParameter("ticket");
            if (!StringUtils.isEmpty(ticket)) {
                UserInfo userInfo = JMockData.mock(UserInfo.class);

                return userInfo;
            }
        }
        return UNRESOLVED;
    }
}

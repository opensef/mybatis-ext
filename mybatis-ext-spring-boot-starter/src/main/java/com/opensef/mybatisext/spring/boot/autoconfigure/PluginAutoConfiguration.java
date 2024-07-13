package com.opensef.mybatisext.spring.boot.autoconfigure;

import com.opensef.mybatisext.autofill.AutoFillHandler;
import com.opensef.mybatisext.autofill.AutoFillPlugin;
import com.opensef.mybatisext.autofill.DefaultAutoFill;
import com.opensef.mybatisext.idhandler.IdGeneratorLong;
import com.opensef.mybatisext.idhandler.IdHandler;
import com.opensef.mybatisext.page.PagePlugin;
import com.opensef.mybatisext.resultmap.ResultMapPlugin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PluginAutoConfiguration {

    /**
     * 因为直接用AutoFillHandler注入，不能注入空对象
     * 用List方式注入，里面可以没有AutoFillHandler对象
     */
    private final List<AutoFillHandler> autoFillHandlerList;
    private final List<IdHandler<?>> idHandlerList;

    public PluginAutoConfiguration(List<AutoFillHandler> autoFillHandlerList, List<IdHandler<?>> idHandlerList) {
        this.autoFillHandlerList = autoFillHandlerList;
        this.idHandlerList = idHandlerList;
    }

    /**
     * 注册AutoFill拦截器插件，用于公共字段填充，需要填充的属性及属性值放入Map集合中
     * 也可以用注解在属性列标注，遍历时解析如果有@AutoFill注解的为其设置属性值，但这种方式没有Map直观，因此选择Map方式
     *
     * @return AutoFillPlugin
     */
    @Bean
    @ConditionalOnMissingBean
    public AutoFillPlugin autoFillPlugin() {
        return new AutoFillPlugin(getAutoFillHandler(), getCustomIdHandler());
    }

    /**
     * 实体自动设置ResultMap插件
     *
     * @return ResultMapPlugin
     */
    // @Bean
    // @ConditionalOnMissingBean
    public ResultMapPlugin resultMapPlugin() {
        return new ResultMapPlugin();
    }

    @Bean
    @ConditionalOnMissingBean
    public PagePlugin pagePlugin() {
        return new PagePlugin();
    }

    /**
     * 获取自动填充处理器
     *
     * @return AutoFillHandler
     */
    private AutoFillHandler getAutoFillHandler() {
        AutoFillHandler fillHandler = null;
        if (null != autoFillHandlerList && autoFillHandlerList.size() > 0) {
            if (autoFillHandlerList.size() > 2) {
                throw new RuntimeException("只能自定义1个自动填充处理器");
            } else if (autoFillHandlerList.size() == 2) {
                // 如果有2个，则选择自定义的
                for (AutoFillHandler autoFillHandler : autoFillHandlerList) {
                    if (autoFillHandler instanceof DefaultAutoFill) {
                        continue;
                    }
                    fillHandler = autoFillHandler;
                }
            } else {
                fillHandler = autoFillHandlerList.get(0);
            }

        } else {
            // 如果没有自定义AutoFillHandler，则设置默认处理器
            fillHandler = new DefaultAutoFill();
        }
        return fillHandler;
    }

    /**
     * 获取自定义id处理器
     *
     * @return IdHandler
     */
    private IdHandler<?> getCustomIdHandler() {
        IdHandler<?> idHandler;
        if (null != idHandlerList && idHandlerList.size() > 0) {
            if (idHandlerList.size() > 2) {
                throw new RuntimeException("只能自定义1个id处理器");
            } else {
                idHandler = idHandlerList.get(0);
            }
        } else {
            idHandler = new IdGeneratorLong();
        }
        return idHandler;
    }

}

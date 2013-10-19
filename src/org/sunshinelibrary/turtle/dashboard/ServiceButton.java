package org.sunshinelibrary.turtle.dashboard;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.Button;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: fengxiaoping
 * Date: 10/20/13
 * Time: 12:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceButton extends Button {

    boolean serviceIsOn = false;
    String serviceName;
    String serviceShortName;
    Context context;
    Class serviceClass;
    ActivityManager am;

    public ServiceButton(Context context) {
        super(context);
        this.context = context;
        setEnabled(false);
        setText("尚未初始化");
    }

    public ServiceButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setEnabled(false);
        setText("尚未初始化");
    }

    public ServiceButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        setEnabled(false);
        setText("尚未初始化");
    }

    public void init(Class serviceClass) {
        if (serviceClass != null) {
            am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            this.serviceClass = serviceClass;
            serviceName = serviceClass.getName();
            serviceShortName = serviceClass.getSimpleName();
            setText("初始化中");
        }
    }

    @Override
    public boolean performClick() {
        if (serviceClass != null) {
            setEnabled(false);
            setText("启动中");
            Intent serverIntent = new Intent(context, serviceClass);
            context.startService(serverIntent);
        }
        return super.performClick();
    }

    public void refresh() {
        if (serviceClass != null) {
            List<ActivityManager.RunningServiceInfo> running = am.getRunningServices(Integer.MAX_VALUE);
            for (ActivityManager.RunningServiceInfo service : running) {
                if (serviceName.equals(service.service.getClassName())) {
                    serviceIsOn = true;
                    break;
                }
            }
            setEnabled(!serviceIsOn);
            setText(serviceIsOn ? serviceShortName + "正在运行" : "启动" + serviceShortName + "服务");
        }
    }

}

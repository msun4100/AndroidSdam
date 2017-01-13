package kr.me.sdam.common.event;

import android.app.usage.UsageEvents;

import java.io.Serializable;

import kr.me.sdam.common.CommonResult;

/**
 * Created by KMS on 2017-01-10.
 */
public class EventInfo implements Serializable {
    public static final String MODE_CREATE = "C";
    public static final String MODE_READ = "R";
    public static final String MODE_UPDATE = "U";
    public static final String MODE_DELETE = "D";

    public CommonResult commonResult;
    public String mode;

    public EventInfo(){}
    public EventInfo(CommonResult commonResult, String mode) {
        this.commonResult = commonResult;
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public CommonResult getCommonResult() {
        return commonResult;
    }

    public void setCommonResult(CommonResult commonResult) {
        this.commonResult = commonResult;
    }
}

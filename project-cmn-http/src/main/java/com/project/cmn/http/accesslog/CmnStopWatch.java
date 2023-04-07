package com.project.cmn.http.accesslog;

import org.springframework.util.StopWatch;

import java.text.NumberFormat;

/**
 * Spring Framework 가 버전 업 되면서 경과시간이 millisecond 에서 nanosecond 로 변경됨.
 * nanosecond 가 보기에 불편해 {@link StopWatch} 를 상속받아 출력하는 부분만 millisecond 로 표현하도록 변경함
 */
public class CmnStopWatch extends StopWatch {
    private boolean keepTaskList;

    /**
     * 생성자
     *
     * @param id 아이디
     */
    public CmnStopWatch(String id) {
        super(id);
        this.keepTaskList = true;
    }

    @Override
    public void setKeepTaskList(boolean keepTaskList) {
        this.keepTaskList = keepTaskList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String shortSummary() {
        return "StopWatch '" + this.getId() + "': running time = " + (this.getTotalTimeNanos() / 1000000) + " ms";
    }

    /**
     * 모든 결과를 포맷팅하여 반환
     *
     * @return 포맷팅된 결과
     */
    public String prettyPrintMillis() {
        StringBuilder sb = new StringBuilder(this.shortSummary());
        sb.append('\n');
        if (!this.keepTaskList) {
            sb.append("No task info kept");
        } else {
            sb.append("---------------------------------------------\n");
            sb.append("ms     %     Task name\n");
            sb.append("---------------------------------------------\n");
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMinimumIntegerDigits(5);
            nf.setGroupingUsed(false);
            NumberFormat pf = NumberFormat.getPercentInstance();
            pf.setMinimumIntegerDigits(3);
            pf.setGroupingUsed(false);
            TaskInfo[] var4 = this.getTaskInfo();
            int var5 = var4.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                TaskInfo task = var4[var6];
                sb.append(nf.format(task.getTimeNanos() / 1000000)).append("  ");
                sb.append(pf.format((double) task.getTimeNanos() / (double) this.getTotalTimeNanos())).append("  ");
                sb.append(task.getTaskName()).append('\n');
            }
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.shortSummary());
        if (this.keepTaskList) {
            TaskInfo[] var2 = this.getTaskInfo();
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                TaskInfo task = var2[var4];
                sb.append("; [").append(task.getTaskName()).append("] took ").append(task.getTimeNanos() / 1000000).append(" ms");
                long percent = Math.round(100.0 * task.getTimeNanos() / this.getTotalTimeNanos());
                sb.append(" = ").append(percent).append('%');
            }
        } else {
            sb.append("; no task info kept");
        }

        return sb.toString();
    }
}

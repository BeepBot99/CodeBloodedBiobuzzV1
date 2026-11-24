package org.codeblooded.ftcodesim.hardware.devices;

import dev.nextftc.telemetry.ftc.DriverStationBackend;
import dev.nextftc.telemetry.ftc.FtcTelemetry;
import org.codeblooded.ftcodesim.simulator.FTCodeSim;
import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.psilynx.psikit.core.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

public class SimTelemetry implements Telemetry {
    private final Map<String, Object> data = new LinkedHashMap<>();
    private final Map<String, Func<?>> producers = new LinkedHashMap<>();

    private final boolean transmit;
    private final FTCodeSim driverStation;

    private boolean autoClear = true;
    private String itemSeparator = " | ";
    private String captionValueSeparator = ": ";

    public SimTelemetry(FTCodeSim driverStation, boolean transmit) {
        dev.nextftc.telemetry.Telemetry.getRegistry().register(new DriverStationBackend(this));
        this.driverStation = driverStation;
        this.transmit = transmit;
    }

    public SimTelemetry(FTCodeSim driverStation) {
        this(driverStation, false);
    }

    public SimTelemetry() {
        this(null, false);
    }

    @Override
    public Item addData(String caption, String format, Object... args) {
        return addData(caption, String.format(format, args));
    }

    @Override
    public Item addData(String caption, Object value) {
        if (value instanceof Double) {
            Logger.recordOutput(caption, (Double) value);
        } else {
            Logger.recordOutput(caption, value.toString());
        }
        data.put(caption, value);
        producers.remove(caption);
        return null;
    }

    @Override
    public <T> Item addData(String caption, Func<T> valueProducer) {
//        producers.put(caption, valueProducer);
//        data.remove(caption);
        return null;
    }

    @Override
    public <T> Item addData(String caption, String format, Func<T> valueProducer) {
        //return addData(caption, () -> String.format(format, valueProducer.value()));
        try {
            throw  new Exception();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean removeItem(Item item) {
        return false;
    }

    @Override
    public void clear() {
        data.clear();
        producers.clear();
    }

    @Override
    public void clearAll() {
        clear();
    }

    @Override
    public Object addAction(Runnable action) {
        return null;
    }

    @Override
    public boolean removeAction(Object token) {
        return false;
    }

    @Override
    public void speak(String text) {}

    @Override
    public void speak(String text, String languageCode, String countryCode) {}

    @Override
    public boolean update() {
        if (data.isEmpty() && producers.isEmpty()) {
            return false;
        }

        Map<String, Object> snapshot = new LinkedHashMap<>(data);

        for (Map.Entry<String, Func<?>> entry : producers.entrySet()) {
            snapshot.put(entry.getKey(), entry.getValue().value());
        }

        StringBuilder output = new StringBuilder();

        for (Map.Entry<String, Object> entry : snapshot.entrySet()) {
            if (output.length() > 0) {
                output.append(itemSeparator);
            }

//            Fate.log(entry.getKey(), entry.getValue());

            output.append(entry.getKey())
                    .append(captionValueSeparator)
                    .append(entry.getValue());
        }

        String telemetry = output.toString();

        if (transmit && driverStation != null) {
            driverStation.sendTelemetry(telemetry);
        }


        if (autoClear) {
            clear();
        }

        return true;
    }

    @Override
    public Line addLine() {
        return addLine("");
    }

    @Override
    public Line addLine(String lineCaption) {
        addData(lineCaption, "");
        return null;
    }

    @Override
    public boolean removeLine(Line line) {
        return false;
    }

    @Override
    public boolean isAutoClear() {
        return autoClear;
    }

    @Override
    public void setAutoClear(boolean autoClear) {
        this.autoClear = autoClear;
    }

    @Override
    public int getMsTransmissionInterval() {
        return 0;
    }

    @Override
    public void setMsTransmissionInterval(int msTransmissionInterval) {}

    @Override
    public String getItemSeparator() {
        return itemSeparator;
    }

    @Override
    public void setItemSeparator(String itemSeparator) {
        this.itemSeparator = itemSeparator;
    }

    @Override
    public String getCaptionValueSeparator() {
        return captionValueSeparator;
    }

    @Override
    public void setCaptionValueSeparator(String captionValueSeparator) {
        this.captionValueSeparator = captionValueSeparator;
    }

    @Override
    public void setDisplayFormat(DisplayFormat displayFormat) {}

    @Override
    public Log log() {
        return null;
    }
}
package kr.kro.probono;

import android.bluetooth.le.ScanResult;

public interface OnCheckModelListener {

    boolean isChecked(byte[] bytes);
    void scannedDevice(ScanResult scanResult);
}

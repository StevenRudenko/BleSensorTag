package sample.ble.sensortag.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;

public class BleDevicesScanner implements Runnable, BluetoothAdapter.LeScanCallback {
    private static final String TAG = BleDevicesScanner.class.getSimpleName();

    private static final long DEFAULT_SCAN_PERIOD = 500L;
    public static final long PERIOD_SCAN_ONCE = -1;

    private final BluetoothAdapter bluetoothAdapter;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private final LeScansPoster leScansPoster;

    private long scanPeriod = DEFAULT_SCAN_PERIOD;
    private Thread scanThread;
    private volatile boolean isScanning = false;

    public BleDevicesScanner(BluetoothAdapter adapter, BluetoothAdapter.LeScanCallback callback) {
        if (adapter == null)
            throw new IllegalArgumentException("Adapter should not be null");

        bluetoothAdapter = adapter;

        leScansPoster = new LeScansPoster(callback);
    }

    public synchronized void setScanPeriod(long scanPeriod) {
        this.scanPeriod = scanPeriod < 0 ? PERIOD_SCAN_ONCE : scanPeriod;
    }

    public boolean isScanning() {
        return scanThread != null && scanThread.isAlive();
    }

    public synchronized void start() {
        if (isScanning())
            return;

        if (scanThread != null) {
            scanThread.interrupt();
        }
        scanThread = new Thread(this);
        scanThread.setName(TAG);
        scanThread.start();
    }

    public synchronized void stop() {
        isScanning = false;
        if (scanThread != null) {
            scanThread.interrupt();
            scanThread = null;
        }
        bluetoothAdapter.stopLeScan(this);
    }

    @Override
    public void run() {
        try {
            isScanning = true;
            do {
                synchronized (this) {
                    bluetoothAdapter.startLeScan(this);
                }

                if (scanPeriod > 0)
                    Thread.sleep(scanPeriod);

                synchronized (this) {
                    bluetoothAdapter.stopLeScan(this);
                }
            } while (isScanning && scanPeriod > 0);
        } catch (InterruptedException ignore) {
        } finally {
            synchronized (this) {
                bluetoothAdapter.stopLeScan(this);
            }
        }
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        synchronized (leScansPoster) {
            leScansPoster.set(device, rssi, scanRecord);
            mainThreadHandler.post(leScansPoster);
        }
    }

    private static class LeScansPoster implements Runnable {
        private final BluetoothAdapter.LeScanCallback leScanCallback;

        private BluetoothDevice device;
        private int rssi;
        private byte[] scanRecord;

        private LeScansPoster(BluetoothAdapter.LeScanCallback leScanCallback) {
            this.leScanCallback = leScanCallback;
        }

        public void set(BluetoothDevice device, int rssi, byte[] scanRecord) {
            this.device = device;
            this.rssi = rssi;
            this.scanRecord = scanRecord;
        }

        @Override
        public void run() {
            leScanCallback.onLeScan(device, rssi, scanRecord);
        }
    }
}
package kr.kro.probono;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapTapi;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import kr.kro.probono.bluetooth.ConnectedTask;


public class MainActivity extends AppCompatActivity {

    private static final String MAC_ADDRESS = "2C:6D:C1:14:94:EC";

    private static UUID uuid;

    private BluetoothDevice device;
    private TMapTapi tapi;

    private EditText xCoordinate;
    private EditText yCoordinate;
    private EditText destName;
    private EditText poiId;
    private CheckBox exitOnArrive;
    private Button execute;

    private ConnectedTask task;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xCoordinate = findViewById(R.id.view_navigate_destination_x_coordinate_EditText);
        yCoordinate = findViewById(R.id.view_navigate_destination_y_coordinate_EditText);
        destName = findViewById(R.id.view_navigate_destination_EditText);
        poiId = findViewById(R.id.view_navigate_POI_id_EditText);
        exitOnArrive = findViewById(R.id.view_navigate_exit_on_arrive_CheckBox);
        execute = findViewById(R.id.view_navigate_execute_button);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        initBT();

        tapi = new TMapTapi(MainActivity.this);
        tapi.setOnAuthenticationListener(new TMapTapi.OnAuthenticationListenerCallback() {
            @Override
            public void SKTMapApikeySucceed() {
                System.out.println("succeed api key");

                System.out.println("installed : " + tapi.isTmapApplicationInstalled());
                System.out.println(tapi.invokeTmap());

            }

            @Override
            public void SKTMapApikeyFailed(String s) {
                System.out.println("installed :!!!! " + tapi.isTmapApplicationInstalled());
            }
        });
        tapi.setSKTMapAuthentication("l7xx5330b9922ba74d55880d8ce5f1b0a568");


        execute.setOnClickListener(v -> {
            System.out.println("!!!!");
            execute();
        });

    }


    public boolean invokeNavigate(String szDestName, float fX, float fY, int poiid, boolean isAutoClose) {
        if (szDestName != null && szDestName.getBytes().length > 128) {
            byte[] byteTemp = new byte[128];
            System.arraycopy(szDestName.getBytes(), 0, byteTemp, 0, 128);
            szDestName = new String(byteTemp);
        }

        PackageInfo info;
        if (TMapData.invokeStatistics("A0", true)) {
            String szInvokeMessage = "tmap://navigate?referrer=com.skt.Tmap&name=";
            if (szDestName != null && !szDestName.trim().equals("")) {
                szInvokeMessage = szInvokeMessage + szDestName;
            } else {
                szInvokeMessage = szInvokeMessage + "도착지";
            }
            if (fX > 0.0F && fY > 0.0F) {
                szInvokeMessage = szInvokeMessage + "&lon=" + fX + "&lat=" + fY;
            } else if (poiid > 0) {
                szInvokeMessage = szInvokeMessage + "&poiid=" + poiid;
            }
            if (fX > 0.0F && fY > 0.0F || poiid > 0) {
                szInvokeMessage = szInvokeMessage + "&autoclose=" + (isAutoClose ? "y" : "n");
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.setClassName("com.skt.tmap.ku", "com.skt.tmap.ku.IntroActivity");
                intent.putExtra("url", szInvokeMessage);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);
            }
        }
        return true;
    }


    public boolean invokeTmap() {
        if (TMapData.invokeStatistics("Z0", true)) {
            try {
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.setClassName("com.skt.tmap.ku", "com.skt.tmap.ku.IntroActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }
        return true;
    }

    private void execute() {

        float x;
        float y;

        try {
            x = Float.parseFloat(xCoordinate.getText().toString());
        } catch (NumberFormatException e) {
            System.out.println(e.getLocalizedMessage() + " x");
            return;
        }

        try {
            y = Float.parseFloat(yCoordinate.getText().toString());
        } catch (NumberFormatException e) {
            System.out.println(e.getLocalizedMessage() + " y");
            return;
        }

        int id = 0;

        try {
            id = Integer.parseInt(poiId.getText().toString());
        } catch (NumberFormatException e) {

        }

        System.out.println(destName.getText().toString() + " " + x + " " + y + " " + id + " " + exitOnArrive.isChecked());

        boolean b = invokeNavigate(
                destName.getText().toString(),
                x,
                y,
                id,
                exitOnArrive.isChecked()
        );
        System.out.println(b);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (task != null) task.close();
    }

    private void initBT() {
        System.out.println("target mac address: " + MAC_ADDRESS);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (devices.size() == 0) {
            System.out.println("device not found");
            return;
        }

        for (BluetoothDevice a : devices) {
            System.out.println(a);
            if (a.getAddress().equals(MAC_ADDRESS)) {
                System.out.println("detected - " + a);
                device = a;
                break;
            }
        }
        if (device == null) {
            System.out.println("device not found");
            return;
        }

        System.out.println("device detected - " + device);

        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        try {
            uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            MainActivity.this.task = new ConnectedTask(socket);
            Log.d("Bluetooth", "create a socket for " + device.getName());
            MainActivity.this.task.start();
        } catch (IOException e) {
            Log.d("Bluetooth", "socket create failed " + e.getMessage());
        }

    }

    private void initBluetooth() {
        System.out.println("target mac address: " + MAC_ADDRESS);


        BluetoothProfile.ServiceListener listener = new BluetoothProfile.ServiceListener() {



            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                for (BluetoothDevice a : proxy.getConnectedDevices()) {
                    System.out.println(a);
                    if (a.getAddress().equals(MAC_ADDRESS)) {
                        System.out.println("detected - " + a);
                        device = a;
                        break;
                    }
                }
                if (device == null) {
                    System.out.println("device not found");
                    return;
                }

                System.out.println("device detected - " + device);
                try {
                    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    MainActivity.this.task = new ConnectedTask(device.createRfcommSocketToServiceRecord(uuid));
                    Log.d("Bluetooth", "create a socket for " + device.getName());
                } catch (IOException e) {
                    Log.d("Bluetooth", "socket create failed " + e.getMessage());
                }

                /*
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                System.out.println("connection state = " + proxy.getConnectionState(device));
                if (proxy.getConnectionState(device) == BluetoothProfile.STATE_CONNECTED) {
                    System.out.println("device is now connected");
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();


                    BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);

                    BluetoothGattServer server = bluetoothManager.openGattServer(MainActivity.this, new BluetoothGattServerCallback() {
                        @Override
                        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                            System.out.println("device state changed: " + device + " " + status + " > " + newState);
                            super.onConnectionStateChange(device, status, newState);
                        }
                    });

                    server.connect(device, false);
                    server.addService(new BluetoothGattService(uuid, BluetoothGattService.SERVICE_TYPE_PRIMARY));

                    System.out.println("start sending data to client");
                    try {
                        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
                        if (!socket.isConnected()) socket.connect();
                        DataOutputStream o = new DataOutputStream(socket.getOutputStream());
                        o.writeUTF("hello");
                        o.flush();
                        o.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

                 */
            }


            @Override
            public void onServiceDisconnected(int profile) {
                System.out.println("disconnected: " + profile);
            }

        };
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(this, listener, BluetoothProfile.STATE_CONNECTED);

    }

}
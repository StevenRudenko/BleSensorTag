package sample.ble.sensortag.ui.adapters;

import com.chimeraiot.android.ble.sensor.DeviceDef;
import com.chimeraiot.android.ble.sensor.Sensor;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sample.ble.sensortag.R;
import sample.ble.sensortag.sensor.BaseSensor;
import sample.ble.sensortag.sensor.ti.TiPeriodicalSensor;

/** BLE services and characteristics adapter. */
public class BleServicesAdapter extends BaseExpandableListAdapter {

    public interface OnServiceItemClickListener {

        void onServiceUpdated(BluetoothGattService service);
    }

    private static final String MODE_READ = "R";

    private static final String MODE_NOTIFY = "N";

    private static final String MODE_WRITE = "W";

    private final ArrayList<BluetoothGattService> services;

    private final HashMap<BluetoothGattService, ArrayList<BluetoothGattCharacteristic>>
            characteristics;

    private final LayoutInflater inflater;

    private OnServiceItemClickListener serviceListener;

    /** BLE device definition. */
    @Nullable
    private final DeviceDef def;

    public BleServicesAdapter(Context context, List<BluetoothGattService> gattServices,
            @Nullable DeviceDef def) {
        inflater = LayoutInflater.from(context);

        this.def = def;
        services = new ArrayList<>(gattServices.size());
        characteristics = new HashMap<>(gattServices.size());
        for (BluetoothGattService gattService : gattServices) {
            final List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                    .getCharacteristics();
            characteristics.put(gattService, new ArrayList<>(gattCharacteristics));
            services.add(gattService);
        }
    }

    public void setServiceListener(OnServiceItemClickListener listener) {
        this.serviceListener = listener;
    }

    @Override
    public int getGroupCount() {
        return services.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return characteristics.get(getGroup(groupPosition)).size();
    }

    @Override
    public BluetoothGattService getGroup(int groupPosition) {
        return services.get(groupPosition);
    }

    @Override
    public BluetoothGattCharacteristic getChild(int groupPosition, int childPosition) {
        return characteristics.get(getGroup(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * 100 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
        final GroupViewHolder holder;
        if (convertView == null) {
            holder = new GroupViewHolder();

            convertView = inflater.inflate(R.layout.li_service, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.uuid = (TextView) convertView.findViewById(R.id.uuid);

            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        final BluetoothGattService item = getGroup(groupPosition);
        final String serviceName;
        final String uuid;
        if (item == null) {
            serviceName = "Unknown";
            uuid = null;
        } else if (def == null) {
            serviceName = "Unknown";
            uuid = item.getUuid().toString();
        } else {
            uuid = item.getUuid().toString();
            final Sensor sensor = def.getSensor(uuid);
            if (sensor != null) {
                serviceName = sensor.getName();
            } else {
                serviceName = "Unknown";
            }
        }

        holder.name.setText(serviceName);
        holder.uuid.setText(uuid);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {
        final ChildViewHolder holder;
        if (convertView == null) {
            holder = new ChildViewHolder();

            convertView = inflater.inflate(R.layout.li_characteristic, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.uuid = (TextView) convertView.findViewById(R.id.uuid);
            holder.modes = (TextView) convertView.findViewById(R.id.modes);
            holder.seek = (SeekBar) convertView.findViewById(R.id.seek);
            holder.seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (serviceListener == null || def == null || !fromUser) {
                        return;
                    }

                    final BaseSensor<?> sensor = (BaseSensor<?>) def.getSensor(
                            holder.service.getUuid().toString());
                    if (sensor == null) {
                        return;
                    }

                    if (sensor instanceof TiPeriodicalSensor) {
                        final TiPeriodicalSensor periodicalSensor = (TiPeriodicalSensor) sensor;
                        periodicalSensor.setPeriod(progress + periodicalSensor.getMinPeriod());
                        serviceListener.onServiceUpdated(holder.service);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }

        final BluetoothGattCharacteristic item = getChild(groupPosition, childPosition);

        final String uuid = item.getUuid().toString();
        final String name;
        final String modes = getModeString(item.getProperties());

        holder.service = item.getService();

        final String serviceUUID = item.getService().getUuid().toString();
        final Sensor<?> sensor = def != null ? (Sensor<?>) def.getSensor(serviceUUID) : null;
        // reset visibility
        holder.uuid.setVisibility(View.VISIBLE);
        holder.seek.setVisibility(View.GONE);

        if (sensor != null) {
            name = sensor.getCharacteristicName(uuid);
            if (sensor instanceof BaseSensor) {
                final BaseSensor<?> tiSensor = (BaseSensor<?>) sensor;
                if (tiSensor.isConfigUUID(uuid)) {
                    if (sensor instanceof TiPeriodicalSensor) {
                        final TiPeriodicalSensor periodicalSensor = (TiPeriodicalSensor) sensor;
                        final int max = periodicalSensor.getMaxPeriod() - periodicalSensor
                                .getMinPeriod();
                        final int value = periodicalSensor.getPeriod() - periodicalSensor
                                .getMinPeriod();
                        holder.seek.setMax(max);
                        holder.seek.setProgress(value);
                        holder.seek.setVisibility(View.VISIBLE);
                        holder.uuid.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            name = "Unknown";
        }

        holder.name.setText(name);
        holder.uuid.setText(uuid);
        holder.modes.setText(modes);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static String getModeString(int prop) {
        final StringBuilder modeBuilder = new StringBuilder();
        if ((prop & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            modeBuilder.append(MODE_READ);
        }
        if ((prop & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            if (modeBuilder.length() > 0) {
                modeBuilder.append("/");
            }
            modeBuilder.append(MODE_NOTIFY);
        }
        if ((prop & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
            if (modeBuilder.length() > 0) {
                modeBuilder.append("/");
            }
            modeBuilder.append(MODE_WRITE);
        }
        return modeBuilder.toString();
    }

    private static class GroupViewHolder {

        TextView name;

        TextView uuid;
    }

    private static class ChildViewHolder {

        BluetoothGattService service;

        TextView name;

        TextView uuid;

        TextView modes;

        SeekBar seek;
    }
}

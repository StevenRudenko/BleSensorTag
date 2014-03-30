package sample.ble.sensortag.adapters;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import sample.ble.sensortag.R;
import sample.ble.sensortag.info.TiInfoService;
import sample.ble.sensortag.info.TiInfoServices;
import sample.ble.sensortag.sensor.TiPeriodicalSensor;
import sample.ble.sensortag.sensor.TiSensor;
import sample.ble.sensortag.sensor.TiSensors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by steven on 9/5/13.
 */
public class TiServicesAdapter extends BaseExpandableListAdapter {

    public interface OnServiceItemClickListener {
        public void onServiceUpdated(BluetoothGattService service);
    }

    private static final String MODE_READ = "R";
    private static final String MODE_NOTIFY = "N";
    private static final String MODE_WRITE = "W";

    private final ArrayList<BluetoothGattService> services;
    private final HashMap<BluetoothGattService, ArrayList<BluetoothGattCharacteristic>> characteristics;
    private final LayoutInflater inflater;

    private OnServiceItemClickListener serviceListener;

    public TiServicesAdapter(Context context, List<BluetoothGattService> gattServices) {
        inflater = LayoutInflater.from(context);

        services = new ArrayList<BluetoothGattService>(gattServices.size());
        characteristics = new HashMap<BluetoothGattService, ArrayList<BluetoothGattCharacteristic>>(gattServices.size());
        for (BluetoothGattService gattService : gattServices) {
            final List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            characteristics.put(gattService, new ArrayList<BluetoothGattCharacteristic>(gattCharacteristics));
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
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

        final String uuid = item.getUuid().toString();
        final TiSensor<?> sensor = TiSensors.getSensor(uuid);
        final TiInfoService infoService = TiInfoServices.getService(uuid);

        final String serviceName;

        if (sensor != null)
            serviceName = sensor.getName();
        else if ( infoService != null )
            serviceName = infoService.getName();
        else
            serviceName = "Unknown";

        holder.name.setText(serviceName);
        holder.uuid.setText(uuid);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
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
                    if (serviceListener == null || !fromUser)
                        return;

                    final TiSensor<?> sensor = TiSensors.getSensor(holder.service.getUuid().toString());
                    if (sensor == null)
                        return;

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
        final TiSensor<?> sensor = TiSensors.getSensor(serviceUUID);
        final TiInfoService infoService = TiInfoServices.getService(serviceUUID);

        if ( sensor != null ) {
            name = sensor.getCharacteristicName(uuid);

            if ( sensor.isConfigUUID(uuid) ) {
                if ( sensor instanceof TiPeriodicalSensor) {
                    final TiPeriodicalSensor periodicalSensor = (TiPeriodicalSensor) sensor;

                    final int max = periodicalSensor.getMaxPeriod() - periodicalSensor.getMinPeriod();
                    final int value = periodicalSensor.getPeriod() - periodicalSensor.getMinPeriod();
                    holder.seek.setMax(max);
                    holder.seek.setProgress(value);

                    holder.seek.setVisibility(View.VISIBLE);
                    holder.uuid.setVisibility(View.GONE);
                }
            } else {
                holder.uuid.setVisibility(View.VISIBLE);
                holder.seek.setVisibility(View.GONE);
            }
        } else if (infoService != null) {
            name = infoService.getCharacteristicName(uuid);

            holder.uuid.setVisibility(View.VISIBLE);
            holder.seek.setVisibility(View.GONE);
        } else {
            name = "Unknown";

            holder.uuid.setVisibility(View.VISIBLE);
            holder.seek.setVisibility(View.GONE);
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
            if (modeBuilder.length() > 0)
                modeBuilder.append("/");
            modeBuilder.append(MODE_NOTIFY);
        }
        if ((prop & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
            if (modeBuilder.length() > 0)
                modeBuilder.append("/");
            modeBuilder.append(MODE_WRITE);
        }
        return modeBuilder.toString();
    }

    private static class GroupViewHolder {
        public TextView name;
        public TextView uuid;
    }

    private static class ChildViewHolder {
        public BluetoothGattService service;

        public TextView name;
        public TextView uuid;
        public TextView modes;
        public SeekBar seek;
    }
}

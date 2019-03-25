package com.bt.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends BaseAdapter {
    private List<BluetoothDevice> mDevices;
    private LayoutInflater mInflater;

    public DeviceAdapter(Context context) {
        mDevices = new ArrayList<BluetoothDevice>();
        mInflater = LayoutInflater.from(context);
    }

    public void addDevice(BluetoothDevice device) {
        if (!mDevices.contains(device)) {
            mDevices.add(device);
            notifyDataSetChanged();
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mDevices.get(position);
    }

    public void clear() {
        mDevices.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflater.inflate(R.layout.item_device, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = view.findViewById(R.id.text_address);
            viewHolder.deviceName = view.findViewById(R.id.text_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDevice device = mDevices.get(i);
        final String deviceName = device.getName();

        if (deviceName != null && deviceName.length() > 0) {
            viewHolder.deviceName.setText(deviceName);
        } else {
            viewHolder.deviceName.setText(R.string.title_unknow_device);
        }
        viewHolder.deviceAddress.setText(device.getAddress());

        return view;
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}
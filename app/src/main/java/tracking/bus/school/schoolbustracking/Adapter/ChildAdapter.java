package tracking.bus.school.schoolbustracking.Adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import tracking.bus.school.schoolbustracking.Config;
import tracking.bus.school.schoolbustracking.Models.Child;
import tracking.bus.school.schoolbustracking.R;

public class ChildAdapter
        extends RecyclerView.Adapter<ChildAdapter.MyViewHolder> {
    private List<Child> childrenList;
    private Context context;
    private ArrayList<StorageReference> storageReferences;

    public void setStorageReferences(ArrayList<StorageReference> storageReferences){
        this.storageReferences = storageReferences;
    }
    public void setContext(Context context){
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvParent;
        public TextView tvDriver;
        public ImageView ivPic;
        public TextView tvStatus;
        public TextView tvAge;
        public TextView tvGender;

        public MyViewHolder(View view){
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvParent = (TextView) view.findViewById(R.id.tvParent);
            tvDriver = (TextView) view.findViewById(R.id.tvDriver);
            ivPic = (ImageView) view.findViewById(R.id.ivPic);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            tvAge = (TextView) view.findViewById(R.id.tvAge);
            tvGender = (TextView) view.findViewById(R.id.tvGender);
        }
    }

    public ChildAdapter(List<Child> childrenList){
        this.childrenList = childrenList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        Child d = childrenList.get(position);
        StorageReference storageReference = storageReferences.get(position);
        holder.tvName.setText(d.getName());
        holder.tvParent.setText(d.getParent());
        holder.tvDriver.setText(d.getDriver());
        holder.tvStatus.setText(d.getStatus());
        holder.tvAge.setText(d.getAge());
        holder.tvGender.setText(d.getGender());

        Glide.with(this.context)
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .into(holder.ivPic);

    }

    @Override
    public int getItemCount(){
        return childrenList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.children_item, parent, false);
        return new MyViewHolder(v);
    }

}
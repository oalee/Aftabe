package ir.treeco.aftabe.New.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import ir.treeco.aftabe.New.Object.PackageObject;
import ir.treeco.aftabe.New.View.Activity.LevelsActivityNew;
import ir.treeco.aftabe.New.View.Activity.MainActivity;
import ir.treeco.aftabe.R;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder>{
    private ArrayList<PackageObject>  packageObjects;
    Context context;
    private MainActivity mainActivity;

    public PackageAdapter(Context context, ArrayList<PackageObject> packageObjects, MainActivity mainActivity) {
        this.context = context;
        this.packageObjects = packageObjects;
        this.mainActivity = mainActivity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView;
        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) itemView.findViewById(R.id.itemPackage);
            textView = (TextView) itemView.findViewById(R.id.itemPackageText);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            File file = new File(context.getFilesDir().getPath() + "/" + packageObjects.get(getAdapterPosition()).getId() + ".zip");
            //todo chack md5

            if (!file.exists()) {
                mainActivity.downloadPackage(
                        packageObjects.get(getAdapterPosition()).getUrl(),
                        context.getFilesDir().getPath(),
                        packageObjects.get(getAdapterPosition()).getId(),
                        packageObjects.get(getAdapterPosition()).getName());
            } else {
                Intent intent = new Intent(context, LevelsActivityNew.class);
                intent.putExtra("id", packageObjects.get(getAdapterPosition()).getId());
                context.startActivity(intent);
            }
        }
    }

    @Override
    public PackageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_item_package, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PackageAdapter.ViewHolder viewHolder, int i) {
        viewHolder.textView.setText(packageObjects.get(i).getName());
        String a = "file://" + context.getFilesDir().getPath() + "/" + packageObjects.get(i).getId() + ".png";
        Log.e("tes", a);
        Picasso.with(context).load(a).into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        if (packageObjects==null)
            return 0;
        return packageObjects.size();
    }
}

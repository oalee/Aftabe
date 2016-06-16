package ir.treeco.aftabe2.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;

import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.Logger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import ir.treeco.aftabe2.API.Rest.AftabeAPIAdapter;
import ir.treeco.aftabe2.API.Rest.Interfaces.OnPackageBuyListener;
import ir.treeco.aftabe2.Adapter.Cache.PackageSolvedCache;
import ir.treeco.aftabe2.MainApplication;
import ir.treeco.aftabe2.Object.Level;
import ir.treeco.aftabe2.Object.PackageObject;
import ir.treeco.aftabe2.Object.User;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.DownloadTask;
import ir.treeco.aftabe2.Util.FontsHolder;
import ir.treeco.aftabe2.Util.LengthManager;
import ir.treeco.aftabe2.Util.PackageTools;
import ir.treeco.aftabe2.Util.SizeManager;
import ir.treeco.aftabe2.Util.Tools;
import ir.treeco.aftabe2.Util.UiUtil;
import ir.treeco.aftabe2.View.Activity.MainActivity;
import ir.treeco.aftabe2.View.Custom.ToastMaker;
import ir.treeco.aftabe2.View.Dialog.PackagePurchaseDialog;
import ir.treeco.aftabe2.View.Fragment.PackageFragment;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {
    private static final String TAG = "PackageAdapter";
    public static final String PACKAGE_LEVEL_LIST_TAG = "LevelsActivityNew1";
    private ArrayList<PackageObject> packageObjects;
    private Activity context;
    private Tools tools;
    private LengthManager lengthManager;

    public PackageAdapter(Activity context, PackageObject[] packageObjectss) {
        this.context = context;
        this.packageObjects = new ArrayList<>();
        Collections.addAll(packageObjects, packageObjectss);
        tools = new Tools(context);
        lengthManager = ((MainApplication) context.getApplicationContext()).getLengthManager();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, DownloadTask.DownloadTaskListener {
        ImageView imageView;
        TextView textView;
        ImageView packagePrice;
        ImageView packageDone;
        TextView price;

        private long lastTimeClicked = 0;
        private long timeStamp = 1000;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) itemView.findViewById(R.id.itemPackage);
            textView = (TextView) itemView.findViewById(R.id.package_progress_dl);
            price = (TextView) itemView.findViewById(R.id.package_coin_text);
            packagePrice = (ImageView) itemView.findViewById(R.id.package_coin_image);
            packageDone = (ImageView) itemView.findViewById(R.id.package_done);

            if (SizeManager.getScreenWidth() < 800)
                textView.setShadowLayer(0.5f, 1, 1, Color.BLACK);
            textView.setTypeface(FontsHolder.getNumeralSansMedium(v.getContext()));
            UiUtil.setTextViewSize(textView, (int) (SizeManager.getScreenWidth() * 0.3), 0.4f);

            if (SizeManager.getScreenWidth() < 800)
                price.setShadowLayer(0.5f, 1, 1, Color.BLACK);
            price.setTypeface(FontsHolder.getNumeralSansMedium(v.getContext()));
            UiUtil.setTextViewSize(price, (int) (SizeManager.getScreenWidth() * 0.3), 0.15f);


            int packageSize = (int) (SizeManager.getScreenWidth() * 0.47);
//            imageView.getLayoutParams().height = packageSize;
//            imageView.getLayoutParams().width = packageSize;

            UiUtil.setWidth(packagePrice, packageSize);
            UiUtil.setHeight(packagePrice, packageSize);

            int textSize = (int) (packageSize * 0.11);
            UiUtil.setLeftMargin(price, textSize);
            UiUtil.setTopMargin(price, (int) (packageSize * 0.09));
            UiUtil.setWidth(price, (int) (packageSize * 0.22));
            UiUtil.setHeight(price, (int) (packageSize * 0.22));


            price.setText(Tools.numeralStringToPersianDigits("2000"));
            v.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

            if (System.currentTimeMillis() - lastTimeClicked < timeStamp)
                return;

            lastTimeClicked = System.currentTimeMillis();

            final int id = packageObjects.get(getAdapterPosition()).getId();
            final PackageObject packageObject = packageObjects.get(getAdapterPosition());

            File file = new File(context.getFilesDir().getPath() + "/Packages/package_" + id + "/");

            if (!file.exists()) {


                final CoinAdapter coinAdapter = ((MainActivity) context).getCoinAdapter();


                new PackagePurchaseDialog(context, packageObject).setYesClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (coinAdapter.spendCoins(packageObject.getPrice())) {
                            AftabeAPIAdapter.buyPackage(id, new OnPackageBuyListener() {
                                @Override
                                public void onPurchasedBefore() {
                                    //          coinAdapter.earnCoins(packageObject.getPrice());
                                }

                                @Override
                                public void onPurchaseSuccess() {

                                }
                            });
                            PackageSolvedCache.getInstance().onBuyPackage(packageObject.getId());

                            PackageTools.getInstance(context).downloadPackage(packageObject, ViewHolder.this);
                            ToastMaker.show(context, "درحال دانلود....", Toast.LENGTH_SHORT);
                        }
                    }
                }).show();


            } else {
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);

                PackageFragment packageFragment = new PackageFragment();
                packageFragment.setArguments(bundle);

                FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, packageFragment, PACKAGE_LEVEL_LIST_TAG);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        }


        @Override
        public void onProgress(final int progress) {
            Logger.d(TAG, "on progress" + progress);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (textView.getVisibility() != View.VISIBLE)
                        textView.setVisibility(View.VISIBLE);
                    textView.setText(Tools.numeralStringToPersianDigits(progress + "") + "%");

                }
            });
        }

        @Override
        public void onDownloadSuccess() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ToastMaker.show(context, "دانلود شد", Toast.LENGTH_SHORT);

                    packagePrice.setVisibility(View.GONE);
                    price.setVisibility(View.GONE);
                    ColorMatrix matrix = new ColorMatrix();

                    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                    imageView.setColorFilter(filter);

                    textView.setVisibility(View.GONE);
                }
            });

        }

        @Override
        public void onDownloadError(String error) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ToastMaker.show(context, context.getResources().getString(R.string.try_later), Toast.LENGTH_SHORT);

                    textView.setVisibility(View.GONE);
                }
            });

        }
    }

    @Override
    public PackageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_package, viewGroup, false);
        int packageSize = (int) (SizeManager.getScreenWidth() * 0.5);
        v.setLayoutParams(new RecyclerView.LayoutParams(
                packageSize,
                packageSize));

//        int padding = (int) (SizeManager.getScreenWidth() * 0.01 );
//
//        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) v.getLayoutParams();
//        params.leftMargin = padding * 2;
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PackageAdapter.ViewHolder viewHolder, int i) {


        int onePercent = (int) (SizeManager.getScreenWidth() * 0.013);

        if (i % 2 == 0) {
            viewHolder.imageView.setPadding(onePercent, 0, 0, 0);
            UiUtil.setLeftMargin(viewHolder.packagePrice, onePercent);
            UiUtil.setLeftMargin(viewHolder.packageDone, onePercent);
//            UiUtil.setLeftMargin(viewHolder.price, onePercent * 2);

            viewHolder.price.setPadding((int) (onePercent * 2), 0, 0, 0);

        } else {

            viewHolder.imageView.setPadding(0, 0, onePercent, 0);


        }


        int id = packageObjects.get(i).getId();

        DBAdapter dbAdapter = DBAdapter.getInstance(context);

        int packageSize = (int) (SizeManager.getScreenWidth() * 0.47);

        String imagePath =  context.getFilesDir().getPath() + "/package_" + id + "_" + "front" + ".png";

//        Picasso.with(context).load(imagePath).resize( packageSize, packageSize).into(viewHolder.imageView);

        viewHolder.imageView.setImageBitmap(ImageManager.getInstance(context).loadImageFromFilse(imagePath , packageSize , (int) (packageSize * 0.9), ImageManager.ScalingLogic.FIT));

        User myUser = Tools.getCachedUser(context);

        if ((myUser != null && myUser.isPackagePurchased(id))) {
            viewHolder.price.setText(Tools.numeralStringToPersianDigits("0"));

        }
        File file = new File(context.getFilesDir().getPath() + "/Packages/package_" + id + "/");


        Level[] list = dbAdapter.getLevels(id);
        if (list != null && list[list.length - 1].isResolved()) {
            Picasso.with(context).load(R.drawable.package_win).into(viewHolder.packageDone);
            viewHolder.packageDone.setVisibility(View.VISIBLE);
        }


        if (!file.exists()) {

            Picasso.with(context).load(R.drawable.package_price).fit().into(viewHolder.packagePrice);
            viewHolder.packagePrice.setVisibility(View.VISIBLE);
            viewHolder.price.setVisibility(View.VISIBLE);
            viewHolder.price.setText(Tools.numeralStringToPersianDigits(packageObjects.get(i).getPrice() + ""));

            if ((myUser != null && myUser.isPackagePurchased(id))) {
                viewHolder.price.setText(Tools.numeralStringToPersianDigits("0"));

            }
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);

            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            viewHolder.imageView.setColorFilter(filter);

        }

        if (!(new File(context.getFilesDir().getPath() + "/package_" + id + "_" + "front" + ".png").exists())) {
            viewHolder.packagePrice.setVisibility(View.GONE);
            viewHolder.price.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if (packageObjects == null)
            return 0;
        return packageObjects.size();
    }

    public void addPackage(PackageObject packageObject) {
        packageObjects.add(packageObject);
        notifyDataSetChanged();

    }
}

package ng.com.calabaryellowpages.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import ng.com.calabaryellowpages.Model.Category;
import ng.com.calabaryellowpages.R;
import ng.com.calabaryellowpages.pluslist;
import ng.com.calabaryellowpages.util.Parse;
import ng.com.calabaryellowpages.util.volleySingleton;

/**
 * Created by SMILECS on 8/27/16.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
    static final int ADVERT = 3;
    static final int LISTING =1;
    static final int PLUSLIST = 2;
    static final int LOADING = 9;
    ArrayList<Category> model;
    Context c;
    public Adapter(ArrayList<Category> model, Context c){
        this.model = model;
        this.c = c;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView special, title, phone, address, work_days;
        ProgressBar bar;
        NetworkImageView image;
        ImageView phoneIcon;
        Button callButton;
        RelativeLayout homeicon, unit, phonIcon, work;
        public ViewHolder(final View itemView, int type) {
            super(itemView);
            homeicon = (RelativeLayout) itemView.findViewById(R.id.addressRel);
            unit = (RelativeLayout) itemView.findViewById(R.id.specialRel);
            work = (RelativeLayout) itemView.findViewById(R.id.workRel);
            phonIcon = (RelativeLayout) itemView.findViewById(R.id.phoneRel);
            callButton = (Button) itemView.findViewById(R.id.call);

            switch (type){
                case LISTING:
                    title = (TextView) itemView.findViewById(R.id.title);
                    phone = (TextView) itemView.findViewById(R.id.contact);
                    address = (TextView) itemView.findViewById(R.id.address);
                    special = (TextView) itemView.findViewById(R.id.specialisation);
                    work_days = (TextView) itemView.findViewById(R.id.workingDays);
                    phoneIcon = (ImageView) itemView.findViewById(R.id.phone_icon);
                    callButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Category cat = (Category) title.getTag();
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + Parse.Parse(cat.getPhone())[0]));
                            itemView.getContext().startActivity(callIntent);
                        }
                    });
                    break;
                case PLUSLIST:
                    image = (NetworkImageView) itemView.findViewById(R.id.profile_image);
                    title = (TextView) itemView.findViewById(R.id.title);
                    phone = (TextView) itemView.findViewById(R.id.contact);
                    address = (TextView) itemView.findViewById(R.id.address);
                    special = (TextView) itemView.findViewById(R.id.specialisation);
                    phoneIcon = (ImageView) itemView.findViewById(R.id.phone_icon);
                    work_days = (TextView) itemView.findViewById(R.id.workingDays);
                    callButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Category cat = (Category) title.getTag();
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + Parse.Parse(cat.getPhone())[0]));
                            itemView.getContext().startActivity(callIntent);
                        }
                    });
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(itemView.getContext(), pluslist.class);
                            i.putExtra("data", (Category) title.getTag());
                            itemView.getContext().startActivity(i);
                        }
                    });
                    break;
                case ADVERT:
                    image = (NetworkImageView) itemView.findViewById(R.id.imageView);
                    bar = (ProgressBar) itemView.findViewById(R.id.progress);
                    break;
            }
                  }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Adapter.ViewHolder vh;
        switch (viewType){
            case LOADING:
                View v0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false);
                vh = new ViewHolder(v0, viewType);
                break;
            case LISTING:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
                vh = new ViewHolder(v, viewType);
                break;
            case PLUSLIST:
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.pluslist, parent, false);
                vh = new ViewHolder(v1, viewType);
                break;
            case ADVERT:
                View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.advert, parent, false);
                vh = new ViewHolder(v2, viewType);
                break;
            default:
                View v3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
                vh = new ViewHolder(v3, viewType);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Category mode = model.get(position);
        int type = 0;
        try{
            if(mode.getType().equals("true")){
                type =  PLUSLIST;
            }else if(mode.getType().equals("false")){
                type = LISTING;
            }else {
                type = ADVERT;
            }
        }catch (Exception e){
            e.printStackTrace();
            type = LISTING;
        }
        Typeface robotMedium = Typeface.createFromAsset(c.getAssets(),
                "fonts/Roboto-Medium.ttf");
        Typeface robotBold = Typeface.createFromAsset(c.getAssets(),
                "fonts/Roboto-Black.ttf");
        Typeface robotLight = Typeface.createFromAsset(c.getAssets(),
                "fonts/Roboto-Thin.ttf");
        Typeface robot = Typeface.createFromAsset(c.getAssets(),
                "fonts/Roboto-Thin.ttf");
        Typeface address = Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-LightItalic.ttf");
        Typeface regular = Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-Regular.ttf");


        switch (type){
            case LISTING:
                holder.title.setTypeface(regular);
                holder.address.setTypeface(address);
                holder.special.setTypeface(robotBold);
                holder.phone.setTypeface(robot);
                holder.work_days.setTypeface(robot);
                holder.title.setText(mode.getTitle());
                holder.title.setTag(mode);
                if(mode.getAddress().isEmpty()){
                    holder.homeicon.setVisibility(View.GONE);
                }
                holder.address.setText(mode.getAddress());
                if(mode.getSpecialisation().isEmpty()){
                    holder.unit.setVisibility(View.GONE);
                }
                holder.special.setText(mode.getSpecialisation());
                if(mode.getPhone().isEmpty() || mode.getPhone().length() < 2){
                    holder.phonIcon.setVisibility(View.GONE);
                    holder.callButton.setVisibility(View.GONE);
                }
               holder.phone.setText(mode.getPhone());
                if(mode.getWork_days().isEmpty() || mode.getWork_days().length() < 2){
                    holder.work.setVisibility(View.GONE);
                }
                holder.work_days.setText(mode.getWork_days());
                break;
            case PLUSLIST:
                holder.title.setTypeface(regular);
                holder.address.setTypeface(address);
                holder.special.setTypeface(robotBold);
                holder.phone.setTypeface(robot);
                holder.work_days.setTypeface(robot);
                ImageLoader imageLoader = volleySingleton.getsInstance().getImageLoader();
                /*imageLoader.get(mode.getImage(), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        Image image = new Image(c);
                        byte[] base64 = image.bitmapToByteArray(imageContainer.getBitmap());
                        String base64String = Base64.encodeToString(base64, Base64.CRLF);
                        holder.image.setTag(base64String);
                        holder
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();

                    }
                });*/
                if(mode.getAddress().isEmpty()){
                    holder.homeicon.setVisibility(View.GONE);
                }
                holder.address.setText(mode.getAddress());
                if(mode.getSpecialisation().isEmpty()){
                    holder.unit.setVisibility(View.GONE);
                }
                holder.special.setText(mode.getSpecialisation());
                if(mode.getPhone().isEmpty() || mode.getPhone().length() < 2){
                    holder.phonIcon.setVisibility(View.GONE);
                    holder.callButton.setVisibility(View.GONE);
                }
                holder.phone.setText(mode.getPhone());
                if(mode.getWork_days().isEmpty() || mode.getWork_days().length() < 2){
                    holder.work.setVisibility(View.GONE);
                }
                holder.work_days.setText(mode.getWork_days());
                holder.image.setImageUrl(mode.getImage(), imageLoader);
                holder.title.setText(mode.getTitle());
                holder.title.setTag(mode);
                break;
            case ADVERT:
                ImageLoader imageLoader2 = volleySingleton.getsInstance().getImageLoader();
                imageLoader2.get(mode.getImage(), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        holder.image.setImageBitmap(imageContainer.getBitmap());
                        holder.bar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                });

                break;
        }



    }




    @Override
    public int getItemCount() {
        return model.size();
    }

    @Override
    public int getItemViewType(int position) {
        Category mode = model.get(position);
        if(position == model.size()){
            Log.d("Adapter", "position   " + String.valueOf(position));
            return LOADING;
        }
        try{
            if(mode.getType().equals("true")){
                return PLUSLIST;
            }else if(mode.getType().equals("false")){
                return LISTING;
            }else {
                return ADVERT;
            }
        }catch (Exception e){
            e.printStackTrace();
            return LISTING;
        }

        //return super.getItemViewType(position);
    }
}
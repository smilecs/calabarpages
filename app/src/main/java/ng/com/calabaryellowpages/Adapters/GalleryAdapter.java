package ng.com.calabaryellowpages.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import ng.com.calabaryellowpages.Model.Category;
import ng.com.calabaryellowpages.R;
import ng.com.calabaryellowpages.util.VolleySingleton;

/**
 * Created by SMILECS on 9/10/16.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    ArrayList<Category> model;

    public GalleryAdapter(ArrayList<Category> model){
        this.model = model;

    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        NetworkImageView image;
        public ViewHolder(View itemView) {
            super(itemView);
            image = (NetworkImageView) itemView.findViewById(R.id.gallery);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category mode = model.get(position);
        ImageLoader imageLoader = VolleySingleton.getsInstance().getImageLoader();
        holder.image.setImageUrl(mode.getImage(), imageLoader);


    }

    @Override
    public int getItemCount() {
        Log.d("GalleryAdapter", Integer.toString(model.size()));
        return model.size();
    }
}

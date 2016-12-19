package ng.com.calabaryellowpages.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ng.com.calabaryellowpages.Model.Category;
import ng.com.calabaryellowpages.R;

/**
 * Created by SMILECS on 8/27/16.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{
    ArrayList<Category> model;
    Context c;
    public CategoryAdapter(ArrayList<Category> model, Context c){
        this.model = model;
        this.c = c;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        Context context;
        public ViewHolder(View itemView) {
            super(itemView);
             context = itemView.getContext();
            title = (TextView) itemView.findViewById(R.id.category);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Category category = (Category) title.getTag();
                    Intent i = new Intent(context, ng.com.calabaryellowpages.Category.class);
                    i.putExtra("slug", category.getSlug());
                    i.putExtra("title", category.getTitle());
                    context.startActivity(i);
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        CategoryAdapter.ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category mode = model.get(position);
        Typeface robot = Typeface.createFromAsset(c.getAssets(),
                "fonts/Roboto-Thin.ttf");
        holder.title.setTypeface(robot);
        holder.title.setText(mode.getTitle());
        holder.title.setTag(mode);
    }

    @Override
    public int getItemCount() {
        return model.size();
    }
}

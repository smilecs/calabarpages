package ng.com.calabaryellowpages.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import ng.com.calabaryellowpages.Model.Review;
import ng.com.calabaryellowpages.R;

public class ReviewFragment extends AppCompatDialogFragment {

    public ReviewFragment() {
        // Required empty public constructor
    }
    EditText review;
    Button submit;
    RatingBar rating;
    Review model;
    CustomDialogInterface customDialogInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model = new Review();
        review = (EditText) view.findViewById(R.id.review);
        submit = (Button) view.findViewById(R.id.submit);
        rating = (RatingBar) view.findViewById(R.id.rating);
        review.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.setScore(rating.getNumStars());
                model.setComment(review.getText().toString());
                customDialogInterface.okButtonClicked(model);
            }
        });

    }

    public interface CustomDialogInterface {

        // This is just a regular method so it can return something or
        // take arguments if you like.
        void okButtonClicked(Review value);


    }
}

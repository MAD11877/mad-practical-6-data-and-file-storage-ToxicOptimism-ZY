package sg.edu.np.week_6_whackamole_3_0;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class CustomScoreViewHolder extends RecyclerView.ViewHolder {
    /* Hint:
        1. This is a customised view holder for the recyclerView list @ levels selection page
     */
    //Define variables
    ConstraintLayout levelConstraintLayout;
    TextView levelNameTextView;
    TextView levelScoreTextView;
    private static final String FILENAME = "CustomScoreViewHolder.java";
    private static final String TAG = "Whack-A-Mole3.0!";

    public CustomScoreViewHolder(final View itemView){
        super(itemView);

        /* Hint:
        This method dictates the viewholder contents and links the widget to the objects for manipulation.
         */
        levelConstraintLayout = (ConstraintLayout) itemView.findViewById(R.id.levelConstraintLayout);
        levelNameTextView = (TextView) itemView.findViewById(R.id.levelNameTextView);
        levelScoreTextView = (TextView) itemView.findViewById(R.id.levelScoreTextView);
    }
}

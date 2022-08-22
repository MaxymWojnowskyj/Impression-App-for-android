package com.example.impressionapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;


public class SettingsRecViewAdapter extends RecyclerView.Adapter<SettingsRecViewAdapter.ViewHolder> {

    Context c;
    PersonClass[] people;

    public SettingsRecViewAdapter(Context c, PersonClass[] people) {
        this.c = c;
        this.people = people;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_div,null);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PersonClass person = people[position];

        Glide.with(c).asBitmap().load(person.getImageURL()).into(holder.iconImage);
        holder.fullName.setText(person.getFullName());

        SharedPreferences sharedPreferences = c.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        // selectedPeople is the var name to get the string of select
        // selectedPeople str ex: "Peter Griffin,Joe Swanson," (if person is not selected they wont be found in str)
        String selectedPeople = sharedPreferences.getString("selectedPeople", "");

        // if the current person is a saved selectedPerson then check their checkbox otherwise leave as unchecked
        if (selectedPeople.contains(person.getFullName())) {
            holder.cBox.setChecked(true);
        } else holder.cBox.setChecked(false);

        //holder.cBox.setChecked(person.isSelected());

        // load savedPreferences settings

        //SharedPreferences.Editor editor = sharedPreferences.edit();

        holder.setItemClickListener(new ViewHolder.ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {

                String selectedPeople = sharedPreferences.getString("selectedPeople", "");

                SharedPreferences.Editor editor = sharedPreferences.edit();

                CheckBox cBox = (CheckBox) v;
                PersonClass person = people[pos];

                // if in same instance click on one checkbox it gets saved then on othercheckbox it gets saved without the previously added (gets overwritten)

                if(cBox.isChecked()) {
                    // append the newly clicked person to the saved list of selectedPeople
                    String newSelectedPeople = selectedPeople + person.getFullName() + ',';

                    //Toast.makeText(c, "after checked: "+newSelectedPeople, Toast.LENGTH_LONG).show();

                    editor.putString("selectedPeople", newSelectedPeople);
                    editor.apply();
                    //person.setSelected(true);
                    //selectedPeople.add(person);
                }
                else if (!cBox.isChecked()) {

                    // remove the newly unclicked person from the saved list of selectedPeople
                    String newSelectedPeople = selectedPeople.replace(person.getFullName() + ',', "");

                    //Toast.makeText(c, "after unChecked: "+newSelectedPeople, Toast.LENGTH_LONG).show();

                    editor.putString("selectedPeople", newSelectedPeople);
                    editor.apply();
                    //person.setSelected(false);
                    //selectedPeople.remove(person);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return people.length;
    }


    // holds view for each item in recyclerview
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView iconImage;
        TextView fullName;
        CheckBox cBox;

        ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconImg);
            fullName = itemView.findViewById(R.id.fullName);
            cBox = itemView.findViewById(R.id.cBox);

            cBox.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener ic) {
            this.itemClickListener = ic;
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v,getLayoutPosition());
        }
        interface ItemClickListener {
            void onItemClick(View v,int pos);
        }
    }
}

package com.example.PetBoard;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.PetBoard.db.Pet;


public class EditPetFragment extends Fragment implements View.OnClickListener {

    interface SaveChangesListener{
        void saveChanges(Pet pet, boolean add);
    }

    private static final String TAG = "EDIT FRAGMENT";
    private static final String ARGS_EDIT = "edit argument";

    private SaveChangesListener mSaveChangesListener;

    EditText mEditName;
    EditText mEditDescription;
    EditText mEditTags;
    ImageButton mEditPetImage;
    RatingBar mEditRatingBar;

    Pet mPet;

    boolean mEdit;

    Button mSaveButton;

    public EditPetFragment() {
        // Required empty public constructor
    }

    public static EditPetFragment newInstance(boolean edit){
        final Bundle args = new Bundle();
        args.putBoolean(ARGS_EDIT, edit);
        EditPetFragment editPetFragment = new EditPetFragment();
        editPetFragment.setArguments(args);
        return editPetFragment;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Log.d(TAG, "onAttach");

        if (context instanceof SaveChangesListener) { //attaches listener
            mSaveChangesListener = (SaveChangesListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement SaveChangesListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_edit_pet, container, false);

        mEditName = view.findViewById(R.id.name_editText);
        mEditDescription = view.findViewById(R.id.description_editText);
        mEditTags = view.findViewById(R.id.tags_editText);
        mEditRatingBar = view.findViewById(R.id.pet_edit_ratingBar);
        mEditPetImage = view.findViewById(R.id.pet_imageButton);

        mSaveButton = view.findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(this);

        if(mEdit){
            setCurrentAttributes(mPet);
        } else {
            mPet = new Pet();
        }

        return view;
    }
    @Override
    public void onClick(View button){
        if(mEditName.toString().equals("")
            || mEditDescription.toString().equals("")
            || mEditTags.toString().equals("")){
            Toast.makeText(EditPetFragment.this.getContext(), "Pet must have a name, description, and tags", Toast.LENGTH_LONG).show();
        }
        mPet.setName(mEditName.getText().toString());
        mPet.setDescription(mEditDescription.getText().toString());
        mPet.setTags(mEditTags.getText().toString());
        mPet.setRating(Double.valueOf(mEditRatingBar.getRating()));
        mSaveChangesListener.saveChanges(mPet, mEdit);
    }

    private void setCurrentAttributes(Pet pet) {
        mEditName.setText(pet.getName());
        mEditDescription.setText(pet.getDescription());
        mEditTags.setText(pet.getTags());
        mEditRatingBar.setRating(pet.getRating().floatValue());
    }


}
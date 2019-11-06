package com.example.PetBoard;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.PetBoard.db.Pet;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PetDetailFragment extends Fragment implements View.OnClickListener {

    interface ExpandButtonListener{
        void expand(Pet pet);
    }

    private ExpandButtonListener mExpandButtonListener;

    TextView mName;
    TextView mDescription;
    TextView mTags;
    RatingBar mRating;
    ImageButton mImage;
    ImageButton mExpandButton;
    String mFilePath;

    Pet mPet;

    private PetViewModel mPetViewModel;
    private List<Pet> mPets;

    private static final String TAG = "PET_DETAIL_FRAGMENT";


    public static PetDetailFragment newInstance(){
        PetDetailFragment fragment = new PetDetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mPetViewModel = ViewModelProviders.of(this).get(PetViewModel.class);

        mPet = defaultPet();

        final Observer<List<Pet>> petListObserver = new Observer<List<Pet>>() {
            @Override
            public void onChanged(List<Pet> pets) {
                Collections.sort(pets);
                PetDetailFragment.this.mPets = pets;
                if(mPets.size() > 0){
                    mPet = mPets.get(0);
                    setAttributes(mPet);
                } else {
                    mPet = defaultPet();
                }
            }
        };


        mPetViewModel.getAllPets().observe(this, petListObserver);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Log.d(TAG, "onAttach");

        if (context instanceof ExpandButtonListener) { //attaches listener
            mExpandButtonListener = (ExpandButtonListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ExpandButtonListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pet_detail, container, false);
        
        mName = view.findViewById(R.id.name_detail_TextView);
        mDescription = view.findViewById(R.id.pet_description_textView);
        mTags = view.findViewById(R.id.tags_detail_textView);
        mRating = view.findViewById(R.id.pet_ratingBar);
        mImage = view.findViewById(R.id.pet_imageButton);
        mExpandButton = view.findViewById(R.id.expand_button);
        mExpandButton.setOnClickListener(this);

        if(mPet == null){
            mPet = defaultPet();
        }
        setPet(mPet);
        return view;
    }
    @Override
    public void onClick(View v) {
        mExpandButtonListener.expand(mPet);
    }

    public Pet defaultPet(){
        Pet pet = new Pet();
        pet.setName("Fluffy");
        pet.setDescription("Very fluffy");
        pet.setRating(3.0);
        pet.setTags("cat, siberian");
        return pet;
    }

    public void setPet(Pet pet){
        this.mPet = pet;
        setAttributes(mPet);
    }

    public void setAttributes(Pet pet) {
        if(pet == null){
            pet = defaultPet();
        }
        mName.setText(getString(R.string.pet_name,  pet.getName()));
        mDescription.setText(getString(R.string.pet_description, pet.getDescription()));
        mTags.setText(getString(R.string.pet_tags, pet.getTags()));
        if(pet.getPhotoPath() == null){
            mImage.setImageResource(android.R.drawable.ic_menu_camera);
        } else {
            loadImage();
        }
        mRating.setRating(pet.getRating().floatValue());
    }

    public Pet getCurrentPet(){
        return mPet;
    }

    public void loadImage() {

        ImageButton imageButton = mImage; //use index of image button
        String path = mPet.getPhotoPath(); //retrieve index of image button to find file

        if (path != null && !path.isEmpty()){ //check if empty
            Picasso.get() //use picasso to load file into image button
                    .load(new File(path))
                    .error(android.R.drawable.stat_notify_error)
                    .fit()
                    .centerCrop()
                    .into(imageButton, new Callback(){
                        @Override
                        public void onSuccess(){
                            Log.d(TAG, "Image loaded");
                        }
                        @Override
                        public void onError(Exception e){
                            Log.e(TAG, "error loading image", e);
                        }
                    });
        }
    }
}

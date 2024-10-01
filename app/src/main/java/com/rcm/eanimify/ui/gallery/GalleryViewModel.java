package com.rcm.eanimify.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final MutableLiveData<String> mText;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
//package com.rcm.eanimify.ui.gallery;
//
//import android.app.Application;
//
//import androidx.annotation.NonNull;
//import androidx.lifecycle.AndroidViewModel;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.rcm.eanimify.data.local.ImageEntity;
//import com.rcm.eanimify.data.local.ImageRepository;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class GalleryViewModel extends AndroidViewModel {
//    private LiveData<List<ImageEntity>> images;
//    private ImageRepository repository;
//
//    public GalleryViewModel(@NonNull Application application) {
//        super(application);
//        repository = new ImageRepository(application);
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            images = repository.getImagesForUser(user.getUid());
//        } else {
//            // Handle case where user is not logged in
//            images = new MutableLiveData<>(new ArrayList<>()); // Or show an error
//        }
//    }
//
//    public LiveData<List<ImageEntity>> getAllImages() {
//        return images;
//    }
//
//    public void insert(ImageEntity image) {
//        repository.insert(image);
//    }
//}

//package com.rcm.eanimify.ui.gallery;
//
//import android.app.Application;
//
//import androidx.annotation.NonNull;
//import androidx.lifecycle.AndroidViewModel;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import androidx.lifecycle.ViewModel;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.rcm.eanimify.data.local.ImageEntity;
//import com.rcm.eanimify.data.local.ImageRepository;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import kotlinx.coroutines.CoroutineScope;
//
//public class GalleryViewModel extends AndroidViewModel {
//    // TODO: Implement the ViewModel
////    private final MutableLiveData<String> mText;
////
////    public GalleryViewModel() {
////        mText = new MutableLiveData<>();
////        mText.setValue("This is gallery fragment");
////    }
////
////    public LiveData<String> getText() {
////        return mText;
////    }
////    private LiveData<List<ImageEntity>> images;
////    private ImageRepository repository;
////
////
////    public GalleryViewModel(@NonNull Application application) {
////        super(application);
////        repository = new ImageRepository(application);
////        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
////        if (user != null) {
////            images = repository.getImagesForUser(user.getUid());
////        } else {
////            // Handle case where user is not logged in
////            images = new MutableLiveData<>(new ArrayList<>()); // Or show an error
////        }
////    }
////
////    public LiveData<List<ImageEntity>> getAllImages() {
////        return images;
////    }
////
////    public void insert(ImageEntity image) {
////        repository.insert(image);
////    }
//}
# postPc_Final_Project_2021

Trello: https://trello.com/b/GTnFXccK

# working with git:

when you're starting to work on a new feature:
1. make sure that your local main branch is update. open a new branch from the main branch, 
   let's call the branch 'A'
2. during your work, if you want to have changes from the remote main branch, perform a commit and
   then pull from main.
3. when you're done working on the feature and you want your changes to exist in the main branch,
   perform commit, then pull from main, and then push your changes to your'e *remote* branch.
   then, go to git, and create a pull request - from your remote branch, to the remote main branch.
   then, the rest of the group will need to review the pull request and approve it. once it's approved,
   click on the 'merge' button in the pull request - now your changes are in the remote main branch.
   
# uploading and downloading from firebase storage:
## in order to upload:
```
String remoteImgName = "profile_photos/" + newUser.getId(); 
StorageReference storageReference = appInstance.getDataManager().storage.getReference();
StorageReference imgRef = storageReference.child(remoteImgName); 
UploadTask uploadTask = imgRef.putFile(Uri.fromFile(new File(imagePath)));
uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
    @Override
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        String downloadUrl = taskSnapshot.getStorage().getDownloadUrl().toString();
        newUser.setPhoto(downloadUrl);
    };
```

what we did is we uploaded an image from an ```imagePath``` that we have on the phone.
the name of the image in firestore will be the user id and the path is ```profile_photos/user_id```.
after we uploaded it successfully, we an get the download url (although we can access the image in several ways).

## in order to download the image to a local file:
```
String postImName = "post_photos/" + postId + ".jpeg";
StorageReference postImag = dataManager.storage.getReference().child(postImName);
File localPostImFile = null;
try {
    localPostImFile = File.createTempFile("post_photos", "jpeg");
} catch (IOException e) {
    e.printStackTrace();
}
File finalLocalPostImFile = localPostImFile;
postImag.getFile(localPostImFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
    @Override
    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
        holder.postIm.setImageURI(Uri.fromFile(finalLocalPostImFile));
    }
}).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception exception) {
        // todo: something todo?
    }
});
```

here we created a reference to the image by the image path, we created a local file and downloaded
the image into the local file. then we can handle success and errors.


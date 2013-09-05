
/* Get keyhash */
try {
    PackageInfo info = getPackageManager().getPackageInfo(
            "com.gobarnacle", 
            PackageManager.GET_SIGNATURES);
    for (Signature signature : info.signatures) {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(signature.toByteArray());
        Log.d("KeyHash0:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
        }
} catch (NameNotFoundException e) {
	Log.d("KeyHash0:", "errorName");
} catch (NoSuchAlgorithmException e) {
	Log.d("KeyHash0:", "errorAlgo");
}
@Override
public boolean shouldOverrideUrlLoading(WebView view, String url) {
	if (URLUtil.isNetworkUrl(url)) {
		view.loadUrl(url);
		return false;
	}
	if (url.startsWith("intent://")) {
		try {
			Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
			if (intent != null) {
				ResolveInfo info = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
				if (info != null) {
					Uri uri = intent.getData();
					if (uri != null) {
						uri = uri.buildUpon().appendQueryParameter("referrer", getPackageName()).build();
						intent.setData(uri);
					}
					startActivity(intent);
				} else {
					String referrer = null;
					Bundle extras = intent.getExtras();
					if (extras != null && extras.get("market_referrer") != null && extras.get("market_referrer") instanceof String) {
						referrer = intent.getExtras().getString("market_referrer");
						try {
							Uri referrerUri = Uri.parse(referrer);
							referrerUri = referrerUri.buildUpon().appendQueryParameter("referrer", getPackageName()).build();
							referrer = referrerUri.toString();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					Intent marketIntent = new Intent(Intent.ACTION_VIEW);
					Uri.Builder marketUriBuilder = new Uri.Builder();
					marketUriBuilder.scheme("market");
					marketUriBuilder.authority("details");
					marketUriBuilder.appendQueryParameter("id", intent.getPackage());
					if (referrer != null) {
						marketUriBuilder.appendQueryParameter("referrer", referrer);
					}
					marketIntent.setData(marketUriBuilder.build());
					startActivity(marketIntent);
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	return false;
}
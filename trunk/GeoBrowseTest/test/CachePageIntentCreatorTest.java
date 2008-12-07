import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import junit.framework.TestCase;
import android.content.Intent;
import android.net.Uri;

import com.android.geobrowse.CachePageIntentCreator;
import com.android.geobrowse.Destination;
import com.android.geobrowse.UriParser;

public class CachePageIntentCreatorTest extends TestCase {
	public void test() {
		UriParser uriParser = createMock(UriParser.class);
		Uri uri = createMock(Uri.class);
		Intent intent = createMock(Intent.class);

		expect(uriParser.createIntent(Intent.ACTION_VIEW, uri)).andReturn(intent);
		expect(uriParser.parse("http://coord.info/GCFOO")).andReturn(uri);

		replay(uriParser);
		replay(intent);
		CachePageIntentCreator cpic = new CachePageIntentCreator(uriParser);
		assertEquals(cpic.createIntent(new Destination("37 12.234 122 56.789 # GCFOO")), intent);
		verify(uriParser);
		verify(intent);
	}
}

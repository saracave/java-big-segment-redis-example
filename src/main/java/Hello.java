import com.launchdarkly.sdk.*;
import com.launchdarkly.sdk.server.*;
import com.launchdarkly.sdk.server.integrations.*;

import java.net.URI;
import java.time.Duration;

public class Hello {

  // Set SDK_KEY to your LaunchDarkly SDK key.
  static final String SDK_KEY = "sdk-key";

  // Set FEATURE_FLAG_KEY to the feature flag key you want to evaluate.
  static final String FEATURE_FLAG_KEY = "flag_key";
  
  private static void showMessage(String s) {
    System.out.println("*** " + s);
    System.out.println();
  }

  public static void main(String... args) throws Exception {
    if (SDK_KEY.equals("")) {
      showMessage("Please edit Hello.java to set SDK_KEY to your LaunchDarkly SDK key first");
      System.exit(1);
    }

        LDConfig config = new LDConfig.Builder()
        .bigSegments(
          Components.bigSegments(
              Redis.bigSegmentStore().uri(URI.create("redis://your-redis:6379")).prefix("your_prefix")
          )
        )
        .build();

        LDClient client = new LDClient(SDK_KEY, config);

        if (client.isInitialized()) {
            showMessage("SDK successfully initialized!");
        } else {
            showMessage("SDK failed to initialize");
            System.exit(1);
        }

        LDContext simpleContext = LDContext.create(ContextKind.of("user"), "sarah");

        client.identify(simpleContext);

        boolean flagValue = client.boolVariation(FEATURE_FLAG_KEY, simpleContext, false);

        showMessage("Feature flag '" + FEATURE_FLAG_KEY + "' is " + flagValue + " for this context");

        // Here we ensure that the SDK shuts down cleanly and has a chance to deliver analytics
        // events to LaunchDarkly before the program exits. If analytics events are not delivered,
        // the context attributes and flag usage statistics will not appear on your dashboard. In
        // a normal long-running application, the SDK would continue running and events would be
        // delivered automatically in the background.
        client.close();
  }
}

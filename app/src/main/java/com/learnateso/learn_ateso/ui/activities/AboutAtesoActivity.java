package com.learnateso.learn_ateso.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.widget.TextView;

import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.helpers.URLSpanNoUnderline;

public class AboutAtesoActivity extends AppCompatActivity {
    private TextView aboutAtesoText, privacyPolicyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_ateso);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        aboutAtesoText = findViewById(R.id.aboutAtesotextView);
        privacyPolicyTextView = findViewById(R.id.privacyPolicyTextView);

        aboutAtesoText();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void aboutAtesoText(){
        String ateso_bbc = "<html><b><a href=\"https://www.bbc.co.uk/news/world-africa-14530839\">" +
                "facing extinction</a></b></html>";
        String privacy_policy = "<html><b><a href=\"http://www.emtechint.com/page1.html\">" +
                "Privacy Policy</a></b></html>";
        //String ateso_wiki = "<html><b><a href=\"https://en.wikipedia.org/wiki/Teso_language\">" +
          //      "Read more about Ateso</a></b></html>";
        String ateso_wiki = "<html><p>Ateso is a Nilo-Saharan language, spoken by the Teso people of Uganda and Kenya" +
                "                There are an estimated 1.9million native speakers of" +
                "                Ateso found in the Eastern part of Uganda and Northwestern part of Kenya." +
                "                </p>"+
                "<b><a href=\"https://en.wikipedia.org/wiki/Teso_language\">" +
                "Read more about Ateso</a></b>"+
                "<p>"+
                "                In 2011, Ateso was among the languages considered to be " +
                "<b><a href=\"https://www.bbc.co.uk/news/world-africa-14530839\">" +
                "               facing extinction</a></b> and efforts" +
                "                were being made by some native speakers to save the language. This became one of the main" +
                "                motivations for the Learn Ateso app because we couldn't stand any relapse of a similar kind." +
                "</p>"+
                "<p>"+
                "                This app aims at teaching Ateso to anyone interested in learning the beautiful language." +
                "                The vision is huge and we are working on several app features celebrating not only Ateso" +
                "                but Teso too as a whole, and we are glad to have you as part of this history making process." +
                "</p>"+
                "                Thank you" +
                "<br/>"+
                "                Emuria Koliai</html>";

        Spannable atesoBbcLink = (Spannable)fromHtml(ateso_bbc);
        Spannable atesoWikiLink = (Spannable)fromHtml(ateso_wiki);

        Spannable privacyPolicyLink = (Spannable)fromHtml(privacy_policy);

        aboutAtesoText.setMovementMethod(LinkMovementMethod.getInstance());
        privacyPolicyTextView.setMovementMethod(LinkMovementMethod.getInstance());

        Spannable atesoBBC = removeUnderlines(atesoBbcLink);
        Spannable atesoWiki = removeUnderlines(atesoWikiLink);
        Spannable privacyPolicy = removeUnderlines(privacyPolicyLink);

        String aboutAteso = "\nAteso is a Nilo-Saharan language, spoken by the Teso people of Uganda and Kenya" +
                "\nThere are an estimated 1.9million native speakers of " +
                "Ateso found in the Eastern part of Uganda and Northwestern part of Kenya." +
                "\n"+
                "\n" +atesoWiki+
                "\n"+
                "\nIn 2011, Ateso was among the languages considered to be "+atesoBBC+" and efforts" +
                "were being made by some native speakers to save the language. This became one of the main" +
                "motivations for the Learn Ateso app because we couldn't stand any relapse of a similar kind." +
                "\n"+
                "\nThe app aims at teaching Ateso to anyone interested in learning the beautiful language. " +
                "The vision is huge and we are working on several app features celebrating not only Ateso" +
                "but Teso too as a whole, and we are glad to have you as a part of the this history making process." +
                "\n Thank you" +
                "\nEmuria Koliai";

        aboutAtesoText.setText(atesoWiki);
        privacyPolicyTextView.setText(privacyPolicy);
    }

    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY); // for 24 api and more
        } else {
            result = Html.fromHtml(html); // or for older api
        }
        return result;
    }

    public static Spannable removeUnderlines(Spannable p_Text) {
        URLSpan[] spans = p_Text.getSpans(0, p_Text.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = p_Text.getSpanStart(span);
            int end = p_Text.getSpanEnd(span);
            p_Text.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            p_Text.setSpan(span, start, end, 0);
        }
        return p_Text;
    }
}

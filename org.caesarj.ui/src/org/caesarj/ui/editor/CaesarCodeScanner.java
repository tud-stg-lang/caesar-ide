package org.caesarj.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.text.AbstractJavaScanner;
import org.eclipse.jdt.internal.ui.text.JavaWhitespaceDetector;
import org.eclipse.jdt.internal.ui.text.JavaWordDetector;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaColorConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * Caesar code scanner 
 */
public final class CaesarCodeScanner extends AbstractJavaScanner {

    private static class VersionedWordRule extends WordRule {

        private final String fVersion;
        private final boolean fEnable;

        private String fCurrentVersion;

        public VersionedWordRule(
            IWordDetector detector,
            String version,
            boolean enable,
            String currentVersion) {
            super(detector);

            fVersion = version;
            fEnable = enable;
            fCurrentVersion = currentVersion;
        }

        public void setCurrentVersion(String version) {
            fCurrentVersion = version;
        }

        /*
         * @see IRule#evaluate
         */

        public IToken evaluate(ICharacterScanner scanner) {
            IToken token = super.evaluate(scanner);

            if (fEnable) {
                if (fCurrentVersion.equals(fVersion))
                    return token;

                return Token.UNDEFINED;

            } else {
                if (fCurrentVersion.equals(fVersion))
                    return Token.UNDEFINED;

                return token;
            }
        }
    }

    private static final String SOURCE_VERSION =
        "org.eclipse.jdt.core.compiler.source";

    private static String[] fgKeywords = { "abstract", //$NON-NLS-1$
        "break", //$NON-NLS-1$
        "case",
            "catch",
            "class",
            "const",
            "continue",
        //$NON-NLS-5$ //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
        "default", "do", //$NON-NLS-2$ //$NON-NLS-1$
        "else", "extends", //$NON-NLS-2$ //$NON-NLS-1$
        "final", "finally", "for", //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
        "goto", //$NON-NLS-1$
        "if",
            "implements",
            "import",
            "instanceof",
            "interface",
        //$NON-NLS-5$ //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
        "native", "new", //$NON-NLS-2$ //$NON-NLS-1$
        "package",
            "private",
            "protected",
            "public",
        //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
        "return", //$NON-NLS-1$
        "static",
            "super",
            "switch",
            "synchronized",
        //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
        "this",
            "throw",
            "throws",
            "transient",
            "try",
        //$NON-NLS-5$ //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
        "volatile", //$NON-NLS-1$
        "while" //$NON-NLS-1$
    };

    // Caesar keywords
    private static String[] caesarKeywords = {
    	"call",         
        "virtual",
        "override",
        "clean",
        "===",
        "after",
        "around",
        "before",
        "crosscutting",
        "declare",
        "deploy",
        "deployed",
        "pointcut",
        "precedence",
        "privileged",
        "returning",
        "throwing",
        "cflow",
        "target",
        "args",
        "proceed",
        
        "provides",
        "collaboration",
        "provided",
        "expected",
        "wraps",
        "binds",
        "wrappee"
    };

    private static String[] fgNewKeywords = { "assert" }; //$NON-NLS-1$

    private static String[] fgTypes =
        {
            "void",
            "boolean",
            "char",
            "byte",
            "short",
            "strictfp",
            "int",
            "long",
            "float",
            "double" };
    //$NON-NLS-1$ //$NON-NLS-5$ //$NON-NLS-7$ //$NON-NLS-6$ //$NON-NLS-8$ //$NON-NLS-9$  //$NON-NLS-10$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-2$

    private static String[] fgConstants = { "false", "null", "true" };
    //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$

    private static String[] fgTokenProperties =
        {
            IJavaColorConstants.JAVA_KEYWORD,
            IJavaColorConstants.JAVA_STRING,
            IJavaColorConstants.JAVA_DEFAULT };

    private VersionedWordRule fVersionedWordRule;

    /**
     * Creates a Java code scanner
     */
    public CaesarCodeScanner(IColorManager manager, IPreferenceStore store) {
        super(manager, store);

        initialize();
    }

    /*
     * @see AbstractJavaScanner#getTokenProperties()
     */
    protected String[] getTokenProperties() {
        return fgTokenProperties;
    }

    /*
     * @see AbstractJavaScanner#createRules()
     */
    protected List createRules() {

        //      System.err.println("AJCodeScanner.createRules() called");
        List rules = new ArrayList();

        // Add rule for strings and character constants.
        Token token = getToken(IJavaColorConstants.JAVA_STRING);
        rules.add(new SingleLineRule("\"", "\"", token, '\\'));
        //$NON-NLS-2$ //$NON-NLS-1$
        rules.add(new SingleLineRule("'", "'", token, '\\'));
        //$NON-NLS-2$ //$NON-NLS-1$

        // Add generic whitespace rule.
        rules.add(new WhitespaceRule(new JavaWhitespaceDetector()));

        // Add word rule for new keywords, 4077
        Object version = JavaCore.getOptions().get(SOURCE_VERSION);
        if (version instanceof String) {
            fVersionedWordRule =
                new VersionedWordRule(new JavaWordDetector(), "1.4", true, (String) version);
            //$NON-NLS-1$

            token = getToken(IJavaColorConstants.JAVA_KEYWORD);
            for (int i = 0; i < fgNewKeywords.length; i++)
                fVersionedWordRule.addWord(fgNewKeywords[i], token);

            rules.add(fVersionedWordRule);
        }

        // Add word rule for keywords, types, and constants.
        token = getToken(IJavaColorConstants.JAVA_DEFAULT);
        WordRule wordRule = new WordRule(new JavaWordDetector(), token);

        token = getToken(IJavaColorConstants.JAVA_KEYWORD);

        for (int i = 0; i < fgKeywords.length; i++)
            wordRule.addWord(fgKeywords[i], token);

        for (int i = 0; i < caesarKeywords.length; i++)
            wordRule.addWord(caesarKeywords[i], token);

        for (int i = 0; i < fgTypes.length; i++)
            wordRule.addWord(fgTypes[i], token);

        for (int i = 0; i < fgConstants.length; i++)
            wordRule.addWord(fgConstants[i], token);

        rules.add(wordRule);

        setDefaultReturnToken(getToken(IJavaColorConstants.JAVA_DEFAULT));
        return rules;
    }

    /*
     * @see RuleBasedScanner#setRules(IRule[])
     */
    public void setRules(IRule[] rules) {
        int i;
        for (i = 0; i < rules.length; i++)
            if (rules[i].equals(fVersionedWordRule))
                break;

        // not found - invalidate fVersionedWordRule
        if (i == rules.length)
            fVersionedWordRule = null;

        super.setRules(rules);
    }

    /*
     * @see AbstractJavaScanner#affectsBehavior(PropertyChangeEvent)
     */
    public boolean affectsBehavior(PropertyChangeEvent event) {
        return event.getProperty().equals(SOURCE_VERSION)
            || super.affectsBehavior(event);
    }

    /*
     * @see AbstractJavaScanner#adaptToPreferenceChange(PropertyChangeEvent)
     */
    public void adaptToPreferenceChange(PropertyChangeEvent event) {

        if (event.getProperty().equals(SOURCE_VERSION)) {
            Object value = event.getNewValue();

            if (value instanceof String) {
                String s = (String) value;

                if (fVersionedWordRule != null)
                    fVersionedWordRule.setCurrentVersion(s);
            }

        } else if (super.affectsBehavior(event)) {
            super.adaptToPreferenceChange(event);
        }
    }

}

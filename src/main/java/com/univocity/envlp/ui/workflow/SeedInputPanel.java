package com.univocity.envlp.ui.workflow;

import com.univocity.cardano.wallet.addresses.*;
import com.univocity.cardano.wallet.common.*;
import com.univocity.envlp.ui.components.labels.*;
import com.univocity.envlp.ui.components.wordlist.*;
import com.univocity.envlp.wallet.*;

import javax.swing.*;
import java.awt.*;

public class SeedInputPanel extends WorkflowPanel {

	private JPanel seedPanel;
	private SeedPhraseInput seedTextArea;
	private String error;
	private final SeedWordCountSelectionPanel wordCountSelectionPanel;

	private SubtitleLabel subtitleLabel;

	public SeedInputPanel(SeedWordCountSelectionPanel wordCountSelectionPanel) {
		this.wordCountSelectionPanel = wordCountSelectionPanel;
	}

	private JPanel getSeedPanel() {
		if (seedPanel == null) {
			seedPanel = new JPanel(new BorderLayout(0, 10));

			seedPanel.add(updateLabel(), BorderLayout.NORTH);
			seedPanel.add(getSeedTextArea(), BorderLayout.CENTER);
		}
		return seedPanel;
	}

	private SubtitleLabel updateLabel() {
		String msg = "Please type in your " + getWordCount() + " words seed phrase:";
		if (subtitleLabel == null) {
			subtitleLabel = new SubtitleLabel(msg);
		} else {
			subtitleLabel.setText(msg);
		}
		return subtitleLabel;

	}

	private SeedPhraseInput getSeedTextArea() {
		if (seedTextArea == null) {
			seedTextArea = new SeedPhraseInput(Seed.englishMnemonicWords());
		}
		return seedTextArea;
	}

	@Override
	protected String getInputErrorMessage() {
		return error;
	}

	@Override
	protected JComponent getInputComponent() {
		return getSeedPanel();
	}

	@Override
	protected String getTitle() {
		return "Your wallet seed phrase";
	}

	@Override
	protected String getDetails() {
		return "<p>&#8226 Please provide your <b>" + getWordCount() + " words</b> seed phrase to proceed.</p>" +
				"<p>&#8226 An incorrect seed may cause you to restore the <b>wrong</b> wallet.</p>" +
				"<p>&#8226 Ensure <b>only you</b> have access to this phrase before<br>continuing. It won't be stored anywhere.</p>";
	}

	private int getWordCount() {
		Integer count = null;
		if (wordCountSelectionPanel != null) {
			count = wordCountSelectionPanel.getValue();
		}
		if (count == null) {
			return 24;
		}
		return count;
	}

	@Override
	protected Object getValue() {
		error = null;
		String seed = Seed.cleanSeedPhrase(getSeedTextArea().getText());
		if (seed.isEmpty()) {
			error = "Seed phrase cannot be blank";
			return null;
		} else {
			String[] words = seed.split(" ");
			if (words.length != getWordCount()) {
				error = "Seed phrase must have " + getWordCount() + " words instead of " + words.length;
				return null;
			}

			AddressManager addressManager = new WalletService().getAddressManager();
			addressManager.setStyle(wordCountSelectionPanel.getWalletType());

			String privateKey = addressManager.generatePrivateKey(seed);
			if (privateKey == null) {
				error = "Invalid seed phrase";
				return null;
			}
		}

		return seed;
	}

	@Override
	public void activate() {
		setDetails(getDetails());
		updateLabel();
		clear();
	}

	@Override
	protected void clearFields() {
		getSeedTextArea().setText("");
	}

}

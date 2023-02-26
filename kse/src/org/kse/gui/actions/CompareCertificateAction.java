package org.kse.gui.actions;

import java.awt.Toolkit;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import org.kse.crypto.keystore.KeyStoreUtil;
import org.kse.gui.KseFrame;
import org.kse.gui.dialogs.DCompareCertificates;
import org.kse.gui.error.DError;
import org.kse.utilities.history.KeyStoreHistory;
import org.kse.utilities.history.KeyStoreState;

public class CompareCertificateAction extends KeyStoreExplorerAction {

	private static final long serialVersionUID = 1L;

	public CompareCertificateAction(KseFrame kseFrame) {
		super(kseFrame);
		putValue(LONG_DESCRIPTION, res.getString("CompareCertificateAction.statusbar"));
		putValue(NAME, res.getString("CompareCertificateAction.text"));
		putValue(SHORT_DESCRIPTION, res.getString("CompareCertificateAction.tooltip"));
		putValue(SMALL_ICON, new ImageIcon(
				Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/genkeypair.png"))));
	}

	@Override
	protected void doAction() {

		List<Certificate> listCertificate = getCertificates();
		if (listCertificate != null) {
			DCompareCertificates dialog = new DCompareCertificates(listCertificate);
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		}
	}

	private List<Certificate> getCertificates() {
		KeyStoreHistory history = kseFrame.getActiveKeyStoreHistory();
		KeyStoreState currentState = history.getCurrentState();

		String[] aliases = kseFrame.getSelectedEntryAliases();

		if (aliases.length < 2) {
			return null;
		}
		try {
			List<Certificate> listCertificates = new ArrayList<>();
			KeyStore keyStore = currentState.getKeyStore();
			for (String alias : aliases) {
				if (KeyStoreUtil.isTrustedCertificateEntry(alias, keyStore)
						|| KeyStoreUtil.isKeyPairEntry(alias, keyStore)) {
					Certificate certificate = keyStore.getCertificate(alias);
					listCertificates.add(certificate);
				}
				// first two certificates found
				if (listCertificates.size() == 2) {
					return listCertificates;
				}
			}
			return null;
		} catch (Exception ex) {
			DError.displayError(frame, ex);
			return null;
		}
	}

}

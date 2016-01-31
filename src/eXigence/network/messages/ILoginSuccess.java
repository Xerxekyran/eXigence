package eXigence.network.messages;

import eXigence.domain.PlayerProfile;

public interface ILoginSuccess extends IOutgoingMessage
{
	/**
	 * Setter für das Profil zu dem gültigen Login
	 * @param profil das vollständige Profil des nun eingeloggten Benutzers
	 */
	public void setProfil(PlayerProfile profil);
}

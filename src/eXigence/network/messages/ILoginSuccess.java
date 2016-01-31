package eXigence.network.messages;

import eXigence.domain.PlayerProfile;

public interface ILoginSuccess extends IOutgoingMessage
{
	/**
	 * Setter f�r das Profil zu dem g�ltigen Login
	 * @param profil das vollst�ndige Profil des nun eingeloggten Benutzers
	 */
	public void setProfil(PlayerProfile profil);
}

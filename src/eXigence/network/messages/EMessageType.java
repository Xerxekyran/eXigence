package eXigence.network.messages;

/**
 * Aufzählung der Nachrichtenarten die es gibt
 * 
 * @author Lars George
 *
 */
public enum EMessageType 
{
	VersionMessage,
	LoginRequest,
	LoginSuccess,
	LoginFailed,
	Disconnect,	
	RegisterRequest,
	RegisterSuccess,
	RegisterFailed,
	Chat,
	ChangeProfileRequest,
	ChangeProfileFailed,
	ChangeProfileSuccess,
	DeleteProfileRequest,
	DeleteProfileSuccess,
	DeleteProfileFailed,
	StatisticRequest,
	Statistics,
	CreateCharacterRequest,
	CreateCharacterSuccess,
	CreateCharacterFailed,
	UpdateCharacters,
	WorldDataRequest,
	WorldData,
	ControllingCharacterBeginFailed,
	ControllingCharacterBeginSuccess,
	ControllingCharacterBeginRequest,
	ControllingCharacterEndFailed,
	ControllingCharacterEndSuccess,
	ControllingCharacterEndRequest,
	Interaction,
	EditCharacterRequest,
	EditCharacterSuccess,
	EditCharacterFailed,
	DeleteCharacterRequest,
	DeleteCharacterSuccess,
	DeleteCharacterFailed
	
}
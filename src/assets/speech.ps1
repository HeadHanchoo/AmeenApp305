Add-Type -AssemblyName System.Speech
$recog = New-Object System.Speech.Recognition.SpeechRecognitionEngine
$recog.LoadGrammar([System.Speech.Recognition.DictationGrammar]::new())
$recog.SetInputToDefaultAudioDevice()

Write-Host "Listening..."
$result = $recog.Recognize()
$result.Text

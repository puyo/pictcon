; Generated NSIS script file (generated by makensitemplate.phtml 0.21)
; by 203.164.185.219 on Jan 14 02 @ 21:32

; NOTE: this .NSI script is designed for NSIS v1.8+

Name "PictorialConsequences"
OutFile "InstallPictcon.exe"

; Some default compiler settings (uncomment and change at will):
; SetCompress auto ; (can be off or force)
; SetDatablockOptimize on ; (can be off)
; CRCCheck on ; (can be off)
; AutoCloseWindow false ; (can be true for the window go away automatically at end)
; ShowInstDetails hide ; (can be show to have them shown, or nevershow to disable)
; SetDateSave off ; (can be on to have files restored to their orginal date)

LicenseText "Copyright (c) Lucy Ding and Gregory McIntyre, 2000, 2001, 2002."
LicenseData "gnudisclaimer.txt"

;InstallDir "$PROGRAMFILES\Pictcon"
InstallDir "$EXEDIR\Pictcon\"
InstallDirRegKey HKEY_LOCAL_MACHINE "SOFTWARE\Lucy Ding and Greg McIntyre\PictorialConsequences" ""
DirShow show ; (make this hide to not let the user change it)
DirText "Select the directory to install Pictorial Consequences in:"

Section "" ; (default section)

SetOutPath "$INSTDIR"
; add files / whatever that need to be installed here.

; Alright... because NSIS doesn't have a simple text input, we have to do this the hard way...
File "E:\lucyd\pictcon\pictcon\install\getjava.exe"
ExecWait 'command.com /c "$INSTDIR\getjava.exe" "$TEMP"'
FileOpen $0 "$TEMP\getjava.out" "r"
FileRead $0 $1
FileClose $0
Delete "$INSTDIR\getjava.exe"
Delete "$TEMP\getjava.out"

MessageBox MB_YESNO $1

File "E:\lucyd\pictcon\pictcon\*.class"
File "E:\lucyd\pictcon\pictcon\*.cfg"
File "E:\lucyd\pictcon\pictcon\*.java"

SetOutPath "$INSTDIR\images\"
File "E:\lucyd\pictcon\pictcon\images\*.*"

SetOutPath "$INSTDIR\pictcon\"
File "E:\lucyd\pictcon\pictcon\pictcon\*.class"
File "E:\lucyd\pictcon\pictcon\pictcon\*.java"

SetOutPath "$INSTDIR\pictcon\com\"
File "E:\lucyd\pictcon\pictcon\pictcon\com\*.class"
File "E:\lucyd\pictcon\pictcon\pictcon\com\*.java"

SetOutPath "$INSTDIR\pictcon\dialogs\"
File "E:\lucyd\pictcon\pictcon\pictcon\dialogs\*.class"
File "E:\lucyd\pictcon\pictcon\pictcon\dialogs\*.java"

SetOutPath "$INSTDIR\pictcon\gamewindow\"
File "E:\lucyd\pictcon\pictcon\pictcon\gamewindow\*.class"
File "E:\lucyd\pictcon\pictcon\pictcon\gamewindow\*.java"

SetOutPath "$INSTDIR\pictcon\paperwindow\"
File "E:\lucyd\pictcon\pictcon\pictcon\paperwindow\*.class"
File "E:\lucyd\pictcon\pictcon\pictcon\paperwindow\*.java"

SetOutPath "$INSTDIR\pictcon\queue\"
File "E:\lucyd\pictcon\pictcon\pictcon\queue\*.class"
File "E:\lucyd\pictcon\pictcon\pictcon\queue\*.java"

CreateDirectory "$INSTDIR\saved"

SetOutPath "$INSTDIR"
CreateShortCut "$INSTDIR\PictorialConsequences.lnk" "C:\WINDOWS\java.exe" "PictorialConsequences" "$INSTDIR\images\pictcon.ico" "0" SW_SHOWMINIMIZED

WriteRegStr HKEY_LOCAL_MACHINE "SOFTWARE\Lucy Ding and Greg McIntyre\PictorialConsequences" "" "$INSTDIR"
WriteRegStr HKEY_LOCAL_MACHINE "Software\Microsoft\Windows\CurrentVersion\Uninstall\PictorialConsequences" "DisplayName" "PictorialConsequences (remove only)"
WriteRegStr HKEY_LOCAL_MACHINE "Software\Microsoft\Windows\CurrentVersion\Uninstall\PictorialConsequences" "UninstallString" '"$INSTDIR\uninstpictcon.exe"'

; write out uninstaller
WriteUninstaller "$INSTDIR\uninstpictcon.exe"
SectionEnd ; end of default section


; begin uninstall settings/section
UninstallText "This will uninstall Pictorial Conseuquences from your system"

Section "Uninstall"
; add delete commands to delete whatever files/registry keys/etc you installed here.
Delete "$INSTDIR\images\*.*"
RMDir "$INSTDIR\images"

Delete "$INSTDIR\pictcon\com\*.*"
RMDir "$INSTDIR\pictcon\com"

Delete "$INSTDIR\pictcon\dialogs\*.*"
RMDir "$INSTDIR\pictcon\dialogs"

Delete "$INSTDIR\pictcon\gamewindow\*.*"
RMDir "$INSTDIR\pictcon\gamewindow"

Delete "$INSTDIR\pictcon\paperwindow\*.*"
RMDir "$INSTDIR\pictcon\paperwindow"

Delete "$INSTDIR\pictcon\queue\*.*"
RMDir "$INSTDIR\pictcon\queue"

Delete "$INSTDIR\pictcon\*.*"
RMDir "$INSTDIR\pictcon"

Delete "$INSTDIR\*.*"



Delete "$INSTDIR\uninstpictcon.exe"
DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\Lucy Ding and Greg McIntyre\PictorialConsequences"
DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\PictorialConsequences"
SectionEnd ; end of uninstall section

; eof
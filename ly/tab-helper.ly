\version "2.11.56"

% This file contains helper functions and layout setup
% for writing down guitar tabs efficiently with lilypond.
% 
% Author: Immanuel Albrecht, tabs@zorgk.de
% 
%
% work in progress, old versions/stuff is commented out

%
% Provided functions:
% \sf #string #fret -> returns a note event with pitch and string -> for accords
% \sfd #string #fret #ly:duration -> returns a note event with pitch & string & duration
%
% \stringShift #deltaString {music} -> shifts the strings used to play, but keeps the original pitch
% \stringShift #down {music} -> go one string down (string 7 is the most down string)
% \stringShift #up {music} -> go up one string
% (note: use transpose to shift the frets)
%
% \tabI {music} -> creates a tabulature environment that is suitable for lead sheets
%
%
%
% \gt "QUICK GUITAR MODE STRING" -> creates the music objects described by the string
%
%


\layout{
      indent = 0\cm
      \context { \TabStaff
        \override VerticalAxisGroup #'minimum-Y-extent = #'(-3 . 9.5)
      }


   }



tuning = #'(14 9 5 0 -5 -10 -17)
tuningb = #'(12 7 3 -2 -7 -12)


gitPitchStringFret = #(lambda (saite fret)
  (let* ((sub (- saite 1))
         (lookup (if (< sub 0) 0 sub))
        )
    (+ (list-ref tuning lookup) fret)
  )
)

gitPitchStringFretb = #(lambda (saite fret)
  (let* ((sub (- saite 1))
         (lookup (if (< sub 0) 0 sub))
        )
    (+ (list-ref tuningb lookup) fret)
  )
)



stringfretpitch = #(lambda (saite bund)
     (ly:make-pitch 0 0 (/ (gitPitchStringFret saite bund) 2))
)

stringfretpitchb = #(lambda (saite bund)
     (ly:make-pitch 0 0 (/ (gitPitchStringFretb saite bund) 2))
)



sfdInternal = #(lambda (saite bund dauer) 

           (make-music 'NoteEvent
                       'pitch (stringfretpitch saite bund)
                       'duration dauer
                       'articulations 
		            (list (make-music
                                  'StringNumberEvent
				  'string-number saite
                            ))
		       )
)

sfdInternalb = #(lambda (saite bund dauer) 

           (make-music 'NoteEvent
                       'pitch (stringfretpitchb saite bund)
                       'duration dauer
                       'articulations 
		            (list (make-music
                                  'StringNumberEvent
				  'string-number saite
                            ))
		       )
)


sfd = #(define-music-function (parser location saite bund dauer) (number? number? ly:duration?)

           (make-music 'NoteEvent
                       'pitch (stringfretpitch saite bund)
                       'duration dauer
                       'articulations 
		            (list (make-music
                                  'StringNumberEvent
				  'string-number saite
                            ))
		       )
)

sfdb = #(define-music-function (parser location saite bund dauer) (number? number? ly:duration?)

           (make-music 'NoteEvent
                       'pitch (stringfretpitchb saite bund)
                       'duration dauer
                       'articulations 
		            (list (make-music
                                  'StringNumberEvent
				  'string-number saite
                            ))
		       )
)


sf = #(define-music-function (parser location saite bund) (number? number?)

           (make-music 'NoteEvent
                       'pitch (stringfretpitch saite bund)
                       'articulations 
		            (list (make-music
                                  'StringNumberEvent
				  'string-number saite
                            ))
		       )
)

sfb = #(define-music-function (parser location saite bund) (number? number?)

           (make-music 'NoteEvent
                       'pitch (stringfretpitchb saite bund)
                       'articulations 
		            (list (make-music
                                  'StringNumberEvent
				  'string-number saite
                            ))
		       )
)




stringShiftInternal = #(lambda (delta musexp) 
(let ((result (ly:music-deep-copy musexp)))
 (if (ly:music? result)
  (letrec
     (
      (doChanges (lambda (elt)
                  (stringShiftInternal delta elt)
                 )
      )
      (iterateElements 
            (lambda (liste)
               (if (null? liste) '()
                   (cons (doChanges (car liste))
                         (iterateElements (cdr liste))
                   )
               )
            )
      )
     )
     (if (null? (ly:music-property result 'elements))
       '()
       (set! (ly:music-property result 'elements)
             (iterateElements (ly:music-property result 'elements)
             )
        )
     )
     (if (null? (ly:music-property result 'articulations))
         
       '()

       (set! (ly:music-property result 'articulations)
             (iterateElements (ly:music-property result 'articulations)
             )
        )
     )

     (if (null? (ly:music-property result 'string-number))
        '()
        (set! (ly:music-property result 'string-number)
              (let ((newstring (+ (ly:music-property result 'string-number) delta)))

                   (if (< newstring 1) 1
                       (if (> newstring (length tuning))
                                (length tuning)
                                newstring)
                   )

              )
        )

     )    

     result    
  )
  '()
 )
)
)

stringShiftInternalb = #(lambda (delta musexp) 
(let ((result (ly:music-deep-copy musexp)))
 (if (ly:music? result)
  (letrec
     (
      (doChanges (lambda (elt)
                  (stringShiftInternalb delta elt)
                 )
      )
      (iterateElements 
            (lambda (liste)
               (if (null? liste) '()
                   (cons (doChanges (car liste))
                         (iterateElements (cdr liste))
                   )
               )
            )
      )
     )
     (if (null? (ly:music-property result 'elements))
       '()
       (set! (ly:music-property result 'elements)
             (iterateElements (ly:music-property result 'elements)
             )
        )
     )
     (if (null? (ly:music-property result 'articulations))
         
       '()

       (set! (ly:music-property result 'articulations)
             (iterateElements (ly:music-property result 'articulations)
             )
        )
     )

     (if (null? (ly:music-property result 'string-number))
        '()
        (set! (ly:music-property result 'string-number)
              (let ((newstring (+ (ly:music-property result 'string-number) delta)))

                   (if (< newstring 1) 1
                       (if (> newstring (length tuningb))
                                (length tuningb)
                                newstring)
                   )

              )
        )

     )    

     result    
  )
  '()
 )
)
)




stringShift = #(define-music-function (parser location delta musex) (number? ly:music?)
(stringShiftInternal delta musex)
)

stringShiftb = #(define-music-function (parser location delta musex) (number? ly:music?)
(stringShiftInternalb delta musex)
)


down = #1
up = #-1

hasHalfNoteLengthNoteEvents = #(lambda (muslist)
(if (null? muslist)
     #f
    (  let ((head (car muslist))
            (halfnote (ly:make-duration 1 0 1 1))
            )
	    (if (eqv? (ly:music-property head 'name) 'NoteEvent)
               (if (ly:duration<? (ly:music-property head 'duration) halfnote)
                   (hasHalfNoteLengthNoteEvents (cdr muslist))
		   (if (ly:duration<? halfnote (ly:music-property head 'duration))
                       (hasHalfNoteLengthNoteEvents (cdr muslist))
                       #t
                   )
               )
               (hasHalfNoteLengthNoteEvents (cdr muslist))
            )
    )
)   
)

hasHalfNoteDotLengthNoteEvents = #(lambda (muslist)
(if (null? muslist)
     #f
    (  let ((head (car muslist))
            (halfnote (ly:make-duration 1 1 1 1))
            )
	    (if (eqv? (ly:music-property head 'name) 'NoteEvent)
               (if (ly:duration<? (ly:music-property head 'duration) halfnote)
                   (hasHalfNoteDotLengthNoteEvents (cdr muslist))
		   (if (ly:duration<? halfnote (ly:music-property head 'duration))
                       (hasHalfNoteDotLengthNoteEvents (cdr muslist))
                       #t
                   )
               )
               (hasHalfNoteDotLengthNoteEvents (cdr muslist))
            )
    )
)   
)

isHalfNoteDotLength = #(lambda (noteevt)
    (  let ((head noteevt)
            (halfnote (ly:make-duration 1 1 1 1))
            )

	    (if (eqv? (ly:music-property head 'name) 'NoteEvent)
               (if (ly:duration<? (ly:music-property head 'duration) halfnote)
                   #f
		   (if (ly:duration<? halfnote (ly:music-property head 'duration))
                       #f
                       #t
                   )
               )
               #f
            )
    )   
)


isHalfNoteLength = #(lambda (noteevt)
    (  let ((head noteevt)
            (halfnote (ly:make-duration 1 0 1 1))
            )

	    (if (eqv? (ly:music-property head 'name) 'NoteEvent)
               (if (ly:duration<? (ly:music-property head 'duration) halfnote)
                   #f
		   (if (ly:duration<? halfnote (ly:music-property head 'duration))
                       #f
                       #t
                   )
               )
               #f
            )
    )   
)



halfNoteSkipInternal = #(lambda (musexp)

(let ((result (ly:music-deep-copy musexp)))
 (if (ly:music? result)
  (letrec
     (
      (doChanges (lambda (elt)
                  (halfNoteSkipInternal elt)
                 )
      )
      (iterateElements 
            (lambda (liste)
               (if (or (null? liste) (not (list? liste)) (null? (car liste))) '()
                   (cons (doChanges (car liste))
                         (if (eqv? (ly:music-property (car liste) 'name) 'EventChord)
                             (if (or (hasHalfNoteLengthNoteEvents (ly:music-property (car liste) 'elements))
                                     (hasHalfNoteDotLengthNoteEvents (ly:music-property (car liste) 'elements)) )
                               (cons (make-music 'EventChord
                                       'elements
                                       (list (make-music 'SkipEvent
                                         'duration 
                                         (ly:make-duration 1 0 0 1)
                                       ))
                                     )
                                     (iterateElements (cdr liste))
                               )
                               (iterateElements (cdr liste))
                              )

                              (if (eqv? (ly:music-property (car liste) 'name) 'NoteEvent)
                                   (if (or (isHalfNoteLength (car liste))
                                           (isHalfNoteDotLength (car liste))
                                       )
                                         (cons (make-music 'SkipEvent
                                                      'duration 
                                                       (ly:make-duration 1 0 0 1)
                                                )
                                                (iterateElements (cdr liste))
                                          )
                                                                      
                                     (iterateElements (cdr liste))
                                    )
                                    (iterateElements (cdr liste))
                              )
                         )
                   )
               )
            )
      )
     )
     (if (null? (ly:music-property result 'elements))
       '()
       (set! (ly:music-property result 'elements)
             (iterateElements (ly:music-property result 'elements)
             )
        )
     )


   result
  )
  '()
 )
)
)

halfNoteFix = #(define-music-function (parser location data) (ly:music?)
(halfNoteSkipInternal data)
)

stringTail = #(lambda (st str)
(substring str st (string-length str))
)


checkEscStringLength = #(lambda (data)
(if (not (string? data)) 0
    (let ((len (string-length data))
          )
          (if (eqv? len 0) 0
                (let ((head (string-ref data 0))
                      (tail (stringTail 1 data)))
                      (if (eqv? head '#\' ) 1
                          (if (> len 1)
                               (if (eqv? head '#\\ )
                                  (+ 2 (checkEscStringLength (stringTail 1 tail)))
                                  (+ 1 (checkEscStringLength tail))
                               )
			       (+ 1 (checkEscStringLength tail))
                          )
                      )
                 
                )
          )
    )
)
)

deEscString = #(lambda (s)
(if (string? s)
    (let ((len (string-length s)))
      (if (> len 0)
        (let ((headAsString (substring s 0 1))
              (head (string-ref s 0))
              (tail (stringTail 1 s))
             )
          (cond
                ((eqv? head '#\\ )
		    (if (< len 2)
                        ""
                        (let ((newHead (substring s 1 2))
                              (newTail (stringTail 2 s))
                              )
                          (string-append newHead (deEscString newTail))
                        )
                    )
                )
                (else (string-append headAsString (deEscString tail)))
            )
	  )

          ""
         )
      )
   
    ""
)
)



quickParser = #(lambda (to_parse)
(if (string-null? to_parse)
    (list 'delimiter 'delimiter)

    (let ((ch (string-ref to_parse 0)))
     (if (eqv? ch '#\')
         (let* ((stringlen (+ 1 (checkEscStringLength (stringTail 1 to_parse))))
                (remainder (stringTail stringlen to_parse))
                (stringdata (substring to_parse 1 (- stringlen 1)))
               )
           (cons 'stringliteral (cons (deEscString stringdata)
	            (cons 'delimiter (quickParser remainder))
                 )
           )
         )

     (cons (case ch 
                ((#\0) '0)
                ((#\1) '1)
                ((#\2) '2)
                ((#\3) '3)
                ((#\4) '4)
                ((#\5) '5)
                ((#\6) '6)
                ((#\7) '7)
                ((#\8) '8)
                ((#\9) '9)
                ((#\-) 'skipstring)
                ((#\_) 'skipstringdown)
                ((#\@) 'selectbasestring)
                ((#\.) 'dot)
                ((#\s) 'squeakorskip)
		((#\x) 'palmmute)
                ((#\t) 'tremolo)
                ((#\() 'legatostart)
                ((#\)) 'legatoend)
                ((#\{) 'phrasestart)
                ((#\}) 'phraseend)
                ((#\[) 'beamstart)
                ((#\]) 'beamend)
                ((#\g) 'glissando)
                ((#\~) 'tie)
                ((#\r) 'rest)
                ((#\|) 'barline)
                ((#\/) 'setduration)
                (else 'delimiter)
           )
           (case ch
                 ((#\0) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\3) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\4) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\5) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\6) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\7) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\8) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\9) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\x) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\t) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\() (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\)) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\[) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\]) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\{) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\}) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\r) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\s) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\g) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\|) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\~) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\,) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 ((#\tab) (cons 'delimiter (quickParser (stringTail 1 to_parse))))
                 (else (quickParser (stringTail 1 to_parse)))
           )
     )
     )	   
    )
)
)

quickParserB = #(lambda (to_parse)
(reverse! (cddr (reverse! (quickParser to_parse))))
)


%(display "\nqg: ")
%(if (null? tokens) (display "noTOKEN!!") (display (car tokens)))
%(display " , ")
%(display delimSeriesLength)
%(display " , ")
%(display currentMode)
%(display " , ")
%(display currentString)
%(display " , ")
%(display data)
%(display " , ")
%(display tokens)
%(display "\n")

checkListStart = #(lambda (check against)
(if (null? against)
    #t
    (if (null? check)
        #f
        (if (eqv? (car check) (car against))
            (checkListStart (cdr check) (cdr against))
            #f
        )
    )
)
)

getListBack = #(lambda (liste dropper)
(if (null? dropper)
    liste
    (if (null? liste)
         '()
         (getListBack (cdr liste) (cdr dropper))
    )
)
)

resetDurations = #(lambda (elemlist newduration)
(if (null? elemlist)
    '()
    (let ((head (car elemlist))
          (tail (cdr elemlist)))

     (cons (cond ((not (null? (ly:music-property head 'elements)))
                  (let ((newthing (ly:music-deep-copy head)))
                       (set! (ly:music-property newthing 'elements) (resetDurations (ly:music-property newthing 'elements) newduration))
                       newthing
                  )
                 )

		 ((not (null? (ly:music-property head 'duration)))
                  (let ((newthing (ly:music-deep-copy head)))
                       (set! (ly:music-property newthing 'duration) newduration)
                       newthing
                  )
                 )

                 (else head)
            )
       (resetDurations tail newduration))
    )
)
)

resetDurationsB = #(lambda (obj dura)

 (let ((result (ly:music-deep-copy obj)))
  (if (not (null? (ly:music-property result 'elements)))
      (set! (ly:music-property result 'elements) (resetDurations (ly:music-property result 'elements) dura))
  )

  result
 )
)

quickGenerator = #(lambda (tokens 
                           currentMusicObject delimSeriesLength 
                           baseString currentString 
                           currentDuration
                           currentMode
                           data)


(if (< currentString 1)
      (quickGenerator tokens currentMusicObject delimSeriesLength baseString 1 currentDuration currentMode data)
 (if (> currentString (length tuning))
      (quickGenerator tokens currentMusicObject delimSeriesLength baseString (length tuning) currentDuration currentMode data)
  (if (null? tokens)
    currentMusicObject

    (let ((head (car tokens))
          (tail (cdr tokens))
         )

         (cond
              ((and (not (null? currentMusicObject)) (> delimSeriesLength 1))
                     (cons currentMusicObject
                           (quickGenerator tokens '() 0 baseString baseString currentDuration 'emptyMode '())
                     )
              )

              ((eqv? head 'delimiter) 
                     (quickGenerator tail currentMusicObject (+ delimSeriesLength 1) baseString currentString currentDuration currentMode data))

              ((eqv? head 'selectbasestring)
                   (if (null? tail)
                        (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                        (if (number? (car tail))
                             (quickGenerator (cdr tail) currentMusicObject 0 (car tail) (car tail) currentDuration currentMode data)
			     (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                        )
                   )
              )


	      ((eqv? head 'glissando)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGenerator tail (make-music
                                                          'EventChord
                                                          'elements
                                                           (cons (make-music
                                                                 'GlissandoEvent
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )

	      ((eqv? head 'legatostart)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGenerator tail (make-music
                                                          'EventChord
                                                          'elements
                                                           (cons (make-music
                                                                 'SlurEvent
								 'span-direction -1
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )


	      ((eqv? head 'legatoend)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGenerator tail (make-music
                                                          'EventChord
                                                          'elements
                                                           (cons (make-music
                                                                 'SlurEvent
								 'span-direction 1
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )


	      ((eqv? head 'phrasestart)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGenerator tail (make-music
                                                          'EventChord
                                                          'elements
                                                           (cons (make-music
                                                                 'PhrasingSlurEvent
								 'span-direction -1
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )


	      ((eqv? head 'phraseend)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGenerator tail (make-music
                                                          'EventChord
                                                          'elements
                                                           (cons (make-music
                                                                 'PhrasingSlurEvent
								 'span-direction 1
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )

	((eqv? head 'beamstart)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGenerator tail (make-music
                                                          'EventChord
                                                          'elements
                                                           (cons (make-music
                                                                 'BeamEvent
								 'span-direction -1
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )


	      ((eqv? head 'beamend)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGenerator tail (make-music
                                                          'EventChord
                                                          'elements
                                                           (cons (make-music
                                                                 'BeamEvent
								 'span-direction 1
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )

	      ((eqv? head 'tie)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGenerator tail (make-music
                                                          'EventChord
                                                          'elements
                                                           (cons (make-music
                                                                 'TieEvent
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )

	      ((eqv? head 'tremolo)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGenerator tail (make-music
                                                          'EventChord
                                                          'elements
                                                          (cons (make-music
                                                                 'ArticulationEvent
                                                                 'articulation-type "prall"
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )

	      ((eqv? head 'palmmute)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGenerator tail (make-music
                                                          'EventChord
                                                          'elements
                                                          (cons (make-music
                                                                 'ArticulationEvent
                                                                 'articulation-type "downbow"
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )

	      ((eqv? head 'squeakorskip)
                     (if (eqv? currentMode 'emptyMode)
                         (quickGenerator tail (make-music
                                                          'EventChord
                                                          'elements
                                                          (list (make-music 'SkipEvent
                                                                            'duration currentDuration)
                                                          )
                                                   ) 0 baseString currentString currentDuration 'chordEventMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGenerator tail (make-music
                                                          'EventChord
                                                          'elements
                                                          (cons (make-music
                                                                 'ArticulationEvent
                                                                 'articulation-type "shortfermata"
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )

	      ((eqv? head 'stringliteral)
                     (if (eqv? currentMode 'emptyMode)
                         (quickGenerator (cdr tail) (make-music
                                                          'EventChord
                                                          'elements
                                                          (list (make-music 
								            'TextScriptEvent
                                                                            'direction 1
									    'text (car tail)
									    )
                                                          )
                                                   ) 0 baseString currentString currentDuration 'chordEventMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGenerator (cdr tail) (make-music
                                                          'EventChord
                                                          'elements
                                                          (cons (make-music
								            'TextScriptEvent
                                                                            'direction -1
									    'text (car tail)
                                                                )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )


              ((or (eqv? head '0)
                   (eqv? head '3)
                   (eqv? head '4)
                   (eqv? head '5)
                   (eqv? head '6)
                   (eqv? head '7)
                   (eqv? head '8)
                   (eqv? head '9)
                )

                (cond ((eqv? currentMode 'emptyMode)
		       (quickGenerator tail (make-music
                                                          'EventChord
                                                          'elements
                                                          (list (sfdInternal currentString head currentDuration)
                                                          )
                                                   ) 0 baseString (- currentString 1) currentDuration 'chordEventMode data))

                      ((eqv? currentMode 'chordEventMode)
		       (quickGenerator tail (make-music
                                                          'EventChord
                                                          'elements
                                                          (cons (sfdInternal currentString head currentDuration)
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString (- currentString 1) currentDuration 'chordEventMode data))

                       
                )
              )

              ((or (eqv? head '1)
                   (eqv? head '2)
               )
                (if (not (null? tail))
  	              (if (not (number? (car tail)))
                          (cond ((eqv? currentMode 'emptyMode)
				 (quickGenerator tail (make-music
                                                          'EventChord
                                                          'elements
                                                          (list (sfdInternal currentString head currentDuration)
                                                          )
                                                   ) 0 baseString (- currentString 1) currentDuration 'chordEventMode data))

				((eqv? currentMode 'chordEventMode)
				 (quickGenerator tail (make-music
                                                          'EventChord
                                                          'elements
                                                          (cons (sfdInternal currentString head currentDuration)
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString (- currentString 1) currentDuration 'chordEventMode data))

		          )
                          (let ((newtail (if (or (eqv? (car tail) '1)
                                                 (eqv? (car tail) '2))
                                              (cons 'delimiter (cdr tail))

                                              (cdr tail)
                                          ))
                                )
                          (cond ((eqv? currentMode 'emptyMode)
				 (quickGenerator newtail (make-music
                                                          'EventChord
                                                          'elements
                                                          (list (sfdInternal currentString (+ (* 10 head) (car tail)) currentDuration)
                                                          )
                                                   ) 0 baseString (- currentString 1) currentDuration 'chordEventMode data))

				((eqv? currentMode 'chordEventMode)
				 (quickGenerator newtail (make-music
                                                          'EventChord
                                                          'elements
                                                          (cons (sfdInternal currentString (+ (* 10 head) (car tail)) currentDuration)
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString (- currentString 1) currentDuration 'chordEventMode data))

		          )
                          )
                          
		       )
                )
              )



              ((eqv? head 'skipstring)
                     (quickGenerator tail currentMusicObject 0 baseString (- currentString 1) currentDuration currentMode data))

              ((eqv? head 'skipstringdown)
                     (quickGenerator tail currentMusicObject 0 baseString (+ currentString 1) currentDuration currentMode data))
              

	      ((eqv? head 'rest)
                    (if (eqv? currentMode 'emptyMode)
			(quickGenerator tail     (make-music
                                                          'EventChord
                                                          'elements
                                                          (list (make-music 'RestEvent
                                                                         'duration
                                                                         currentDuration
                                                                )
                                                          )) 0 baseString (- currentString 1) currentDuration 'chordEventMode data)
		         
                         (quickGenerator (cons 'delimiter tokens) currentMusicObject 1000 baseString currentString currentDuration currentMode data)
	            )
              )

	      ((eqv? head 'barline)
                   (if (eqv? currentMode 'emptyMode)
                             (cons 
			           (make-music
				    'ContextSpeccedMusic
				    'context-type
				    'Timing
				    'element
				      (make-music
				       'PropertySet
				       'value
				       "|"
				       'symbol
				       'whichBar))

				   (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                              )
			     (quickGenerator tokens currentMusicObject 1000 baseString currentString currentDuration currentMode data)
		    )
	       )

	      ((eqv? head 'setduration)
                    (let ((delimCounter 2))
                     (cond
                        ((checkListStart tail (quickParserB "4..."))
                          (let (
                                 (newduration (ly:make-duration 2 3 1 1))
                                 (newtail (getListBack tail (quickParserB "4...")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "4.."))
                          (let (
                                 (newduration (ly:make-duration 2 2 1 1))
                                 (newtail (getListBack tail (quickParserB "4..")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

			((checkListStart tail (quickParserB "4."))
                          (let (
                                 (newduration (ly:make-duration 2 1 1 1))
                                 (newtail (getListBack tail (quickParserB "4.")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "4"))
                          (let (
                                 (newduration (ly:make-duration 2 0 1 1))
                                 (newtail (getListBack tail (quickParserB "4")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

                        ((checkListStart tail (quickParserB "8..."))
                          (let (
                                 (newduration (ly:make-duration 3 3 1 1))
                                 (newtail (getListBack tail (quickParserB "8...")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "8.."))
                          (let (
                                 (newduration (ly:make-duration 3 2 1 1))
                                 (newtail (getListBack tail (quickParserB "8..")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

			((checkListStart tail (quickParserB "8."))
                          (let (
                                 (newduration (ly:make-duration 3 1 1 1))
                                 (newtail (getListBack tail (quickParserB "8.")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "8"))
                          (let (
                                 (newduration (ly:make-duration 3 0 1 1))
                                 (newtail (getListBack tail (quickParserB "8")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

                        ((checkListStart tail (quickParserB "16..."))
                          (let (
                                 (newduration (ly:make-duration 4 3 1 1))
                                 (newtail (getListBack tail (quickParserB "16...")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "16.."))
                          (let (
                                 (newduration (ly:make-duration 4 2 1 1))
                                 (newtail (getListBack tail (quickParserB "16..")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

			((checkListStart tail (quickParserB "16."))
                          (let (
                                 (newduration (ly:make-duration 4 1 1 1))
                                 (newtail (getListBack tail (quickParserB "16.")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "16"))
                          (let (
                                 (newduration (ly:make-duration 4 0 1 1))
                                 (newtail (getListBack tail (quickParserB "16")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

                        ((checkListStart tail (quickParserB "32..."))
                          (let (
                                 (newduration (ly:make-duration 5 3 1 1))
                                 (newtail (getListBack tail (quickParserB "32...")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "32.."))
                          (let (
                                 (newduration (ly:make-duration 5 2 1 1))
                                 (newtail (getListBack tail (quickParserB "32..")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

			((checkListStart tail (quickParserB "32."))
                          (let (
                                 (newduration (ly:make-duration 5 1 1 1))
                                 (newtail (getListBack tail (quickParserB "32.")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "32"))
                          (let (
                                 (newduration (ly:make-duration 5 0 1 1))
                                 (newtail (getListBack tail (quickParserB "32")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

                        ((checkListStart tail (quickParserB "64..."))
                          (let (
                                 (newduration (ly:make-duration 6 3 1 1))
                                 (newtail (getListBack tail (quickParserB "64...")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "64.."))
                          (let (
                                 (newduration (ly:make-duration 6 2 1 1))
                                 (newtail (getListBack tail (quickParserB "64..")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

			((checkListStart tail (quickParserB "64."))
                          (let (
                                 (newduration (ly:make-duration 6 1 1 1))
                                 (newtail (getListBack tail (quickParserB "64.")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "64"))
                          (let (
                                 (newduration (ly:make-duration 6 0 1 1))
                                 (newtail (getListBack tail (quickParserB "64")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

                        ((checkListStart tail (quickParserB "2..."))
                          (let (
                                 (newduration (ly:make-duration 1 3 1 1))
                                 (newtail (getListBack tail (quickParserB "2...")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "2.."))
                          (let (
                                 (newduration (ly:make-duration 1 2 1 1))
                                 (newtail (getListBack tail (quickParserB "2..")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

			((checkListStart tail (quickParserB "2."))
                          (let (
                                 (newduration (ly:make-duration 1 1 1 1))
                                 (newtail (getListBack tail (quickParserB "2.")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "2"))
                          (let (
                                 (newduration (ly:make-duration 1 0 1 1))
                                 (newtail (getListBack tail (quickParserB "2")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

                        ((checkListStart tail (quickParserB "1..."))
                          (let (
                                 (newduration (ly:make-duration 0 3 1 1))
                                 (newtail (getListBack tail (quickParserB "1...")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "1.."))
                          (let (
                                 (newduration (ly:make-duration 0 2 1 1))
                                 (newtail (getListBack tail (quickParserB "1..")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

			((checkListStart tail (quickParserB "1."))
                          (let (
                                 (newduration (ly:make-duration 0 1 1 1))
                                 (newtail (getListBack tail (quickParserB "1.")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "1"))
                          (let (
                                 (newduration (ly:make-duration 0 0 1 1))
                                 (newtail (getListBack tail (quickParserB "1")))
                               )
			   (quickGenerator newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

			(else (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data))
                     )
                    )
              )

	      

              (else (quickGenerator tail currentMusicObject 0 baseString currentString currentDuration currentMode data))
         )
    )
  )
 )
)
)

gt = #(define-music-function (parser location data) (string?)
(let ((liste ( quickGenerator (quickParser (string-append data "    ")) '() 0 (length tuning) (length tuning)
                          (ly:make-duration 2 0 1 1) 'emptyMode '()) )
     )

(make-music 'SequentialMusic
        'elements
        liste)
)
)


quickGeneratorb = #(lambda (tokens 
                           currentMusicObject delimSeriesLength 
                           baseString currentString 
                           currentDuration
                           currentMode
                           data)


(if (< currentString 1)
      (quickGeneratorb tokens currentMusicObject delimSeriesLength baseString 1 currentDuration currentMode data)
 (if (> currentString (length tuningb))
      (quickGeneratorb tokens currentMusicObject delimSeriesLength baseString (length tuningb) currentDuration currentMode data)
  (if (null? tokens)
    currentMusicObject

    (let ((head (car tokens))
          (tail (cdr tokens))
         )

         (cond
              ((and (not (null? currentMusicObject)) (> delimSeriesLength 1))
                     (cons currentMusicObject
                           (quickGeneratorb tokens '() 0 baseString baseString currentDuration 'emptyMode '())
                     )
              )

              ((eqv? head 'delimiter) 
                     (quickGeneratorb tail currentMusicObject (+ delimSeriesLength 1) baseString currentString currentDuration currentMode data))

              ((eqv? head 'selectbasestring)
                   (if (null? tail)
                        (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                        (if (number? (car tail))
                             (quickGeneratorb (cdr tail) currentMusicObject 0 (car tail) (car tail) currentDuration currentMode data)
			     (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                        )
                   )
              )


	      ((eqv? head 'glissando)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGeneratorb tail (make-music
                                                          'EventChord
                                                          'elements
                                                           (cons (make-music
                                                                 'GlissandoEvent
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )

	      ((eqv? head 'legatostart)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGeneratorb tail (make-music
                                                          'EventChord
                                                          'elements
                                                           (cons (make-music
                                                                 'SlurEvent
								 'span-direction -1
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )


	      ((eqv? head 'legatoend)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGeneratorb tail (make-music
                                                          'EventChord
                                                          'elements
                                                           (cons (make-music
                                                                 'SlurEvent
								 'span-direction 1
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )


	      ((eqv? head 'phrasestart)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGeneratorb tail (make-music
                                                          'EventChord
                                                          'elements
                                                           (cons (make-music
                                                                 'PhrasingSlurEvent
								 'span-direction -1
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )


	      ((eqv? head 'phraseend)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGeneratorb tail (make-music
                                                          'EventChord
                                                          'elements
                                                           (cons (make-music
                                                                 'PhrasingSlurEvent
								 'span-direction 1
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )

	((eqv? head 'beamstart)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGeneratorb tail (make-music
                                                          'EventChord
                                                          'elements
                                                           (cons (make-music
                                                                 'BeamEvent
								 'span-direction -1
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )


	      ((eqv? head 'beamend)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGeneratorb tail (make-music
                                                          'EventChord
                                                          'elements
                                                           (cons (make-music
                                                                 'BeamEvent
								 'span-direction 1
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )

	      ((eqv? head 'tie)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGeneratorb tail (make-music
                                                          'EventChord
                                                          'elements
                                                           (cons (make-music
                                                                 'TieEvent
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )

	      ((eqv? head 'tremolo)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGeneratorb tail (make-music
                                                          'EventChord
                                                          'elements
                                                          (cons (make-music
                                                                 'ArticulationEvent
                                                                 'articulation-type "prall"
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )

	      ((eqv? head 'palmmute)
                     (if (eqv? currentMode 'emptyMode)
			 (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGeneratorb tail (make-music
                                                          'EventChord
                                                          'elements
                                                          (cons (make-music
                                                                 'ArticulationEvent
                                                                 'articulation-type "downbow"
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )

	      ((eqv? head 'squeakorskip)
                     (if (eqv? currentMode 'emptyMode)
                         (quickGeneratorb tail (make-music
                                                          'EventChord
                                                          'elements
                                                          (list (make-music 'SkipEvent
                                                                            'duration currentDuration)
                                                          )
                                                   ) 0 baseString currentString currentDuration 'chordEventMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGeneratorb tail (make-music
                                                          'EventChord
                                                          'elements
                                                          (cons (make-music
                                                                 'ArticulationEvent
                                                                 'articulation-type "shortfermata"
                                                                 
                                                                 )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )

	      ((eqv? head 'stringliteral)
                     (if (eqv? currentMode 'emptyMode)
                         (quickGeneratorb (cdr tail) (make-music
                                                          'EventChord
                                                          'elements
                                                          (list (make-music 
								            'TextScriptEvent
                                                                            'direction 1
									    'text (car tail)
									    )
                                                          )
                                                   ) 0 baseString currentString currentDuration 'chordEventMode data)

                         (if (eqv? currentMode 'chordEventMode)
			     (quickGeneratorb (cdr tail) (make-music
                                                          'EventChord
                                                          'elements
                                                          (cons (make-music
								            'TextScriptEvent
                                                                            'direction -1
									    'text (car tail)
                                                                )
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString currentString  currentDuration 'chordEventMode data)

                             (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                         )
                     )
              )


              ((or (eqv? head '0)
                   (eqv? head '3)
                   (eqv? head '4)
                   (eqv? head '5)
                   (eqv? head '6)
                   (eqv? head '7)
                   (eqv? head '8)
                   (eqv? head '9)
                )

                (cond ((eqv? currentMode 'emptyMode)
		       (quickGeneratorb tail (make-music
                                                          'EventChord
                                                          'elements
                                                          (list (sfdInternalb currentString head currentDuration)
                                                          )
                                                   ) 0 baseString (- currentString 1) currentDuration 'chordEventMode data))

                      ((eqv? currentMode 'chordEventMode)
		       (quickGeneratorb tail (make-music
                                                          'EventChord
                                                          'elements
                                                          (cons (sfdInternalb currentString head currentDuration)
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString (- currentString 1) currentDuration 'chordEventMode data))

                       
                )
              )

              ((or (eqv? head '1)
                   (eqv? head '2)
               )
                (if (not (null? tail))
  	              (if (not (number? (car tail)))
                          (cond ((eqv? currentMode 'emptyMode)
				 (quickGeneratorb tail (make-music
                                                          'EventChord
                                                          'elements
                                                          (list (sfdInternalb currentString head currentDuration)
                                                          )
                                                   ) 0 baseString (- currentString 1) currentDuration 'chordEventMode data))

				((eqv? currentMode 'chordEventMode)
				 (quickGeneratorb tail (make-music
                                                          'EventChord
                                                          'elements
                                                          (cons (sfdInternalb currentString head currentDuration)
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString (- currentString 1) currentDuration 'chordEventMode data))

		          )
                          (let ((newtail (if (or (eqv? (car tail) '1)
                                                 (eqv? (car tail) '2))
                                              (cons 'delimiter (cdr tail))

                                              (cdr tail)
                                          ))
                                )
                          (cond ((eqv? currentMode 'emptyMode)
				 (quickGeneratorb newtail (make-music
                                                          'EventChord
                                                          'elements
                                                          (list (sfdInternalb currentString (+ (* 10 head) (car tail)) currentDuration)
                                                          )
                                                   ) 0 baseString (- currentString 1) currentDuration 'chordEventMode data))

				((eqv? currentMode 'chordEventMode)
				 (quickGeneratorb newtail (make-music
                                                          'EventChord
                                                          'elements
                                                          (cons (sfdInternalb currentString (+ (* 10 head) (car tail)) currentDuration)
                                                                (ly:music-property currentMusicObject 'elements)
                                                          )
                                                   ) 0 baseString (- currentString 1) currentDuration 'chordEventMode data))

		          )
                          )
                          
		       )
                )
              )



              ((eqv? head 'skipstring)
                     (quickGeneratorb tail currentMusicObject 0 baseString (- currentString 1) currentDuration currentMode data))

              ((eqv? head 'skipstringdown)
                     (quickGeneratorb tail currentMusicObject 0 baseString (+ currentString 1) currentDuration currentMode data))
              

	      ((eqv? head 'rest)
                    (if (eqv? currentMode 'emptyMode)
			(quickGeneratorb tail     (make-music
                                                          'EventChord
                                                          'elements
                                                          (list (make-music 'RestEvent
                                                                         'duration
                                                                         currentDuration
                                                                )
                                                          )) 0 baseString (- currentString 1) currentDuration 'chordEventMode data)
		         
                         (quickGeneratorb (cons 'delimiter tokens) currentMusicObject 1000 baseString currentString currentDuration currentMode data)
	            )
              )

	      ((eqv? head 'barline)
                   (if (eqv? currentMode 'emptyMode)
                             (cons 
			           (make-music
				    'ContextSpeccedMusic
				    'context-type
				    'Timing
				    'element
				      (make-music
				       'PropertySet
				       'value
				       "|"
				       'symbol
				       'whichBar))

				   (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data)
                              )
			     (quickGeneratorb tokens currentMusicObject 1000 baseString currentString currentDuration currentMode data)
		    )
	       )

	      ((eqv? head 'setduration)
                    (let ((delimCounter 2))
                     (cond
                        ((checkListStart tail (quickParserB "4..."))
                          (let (
                                 (newduration (ly:make-duration 2 3 1 1))
                                 (newtail (getListBack tail (quickParserB "4...")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "4.."))
                          (let (
                                 (newduration (ly:make-duration 2 2 1 1))
                                 (newtail (getListBack tail (quickParserB "4..")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

			((checkListStart tail (quickParserB "4."))
                          (let (
                                 (newduration (ly:make-duration 2 1 1 1))
                                 (newtail (getListBack tail (quickParserB "4.")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "4"))
                          (let (
                                 (newduration (ly:make-duration 2 0 1 1))
                                 (newtail (getListBack tail (quickParserB "4")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

                        ((checkListStart tail (quickParserB "8..."))
                          (let (
                                 (newduration (ly:make-duration 3 3 1 1))
                                 (newtail (getListBack tail (quickParserB "8...")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "8.."))
                          (let (
                                 (newduration (ly:make-duration 3 2 1 1))
                                 (newtail (getListBack tail (quickParserB "8..")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

			((checkListStart tail (quickParserB "8."))
                          (let (
                                 (newduration (ly:make-duration 3 1 1 1))
                                 (newtail (getListBack tail (quickParserB "8.")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "8"))
                          (let (
                                 (newduration (ly:make-duration 3 0 1 1))
                                 (newtail (getListBack tail (quickParserB "8")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

                        ((checkListStart tail (quickParserB "16..."))
                          (let (
                                 (newduration (ly:make-duration 4 3 1 1))
                                 (newtail (getListBack tail (quickParserB "16...")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "16.."))
                          (let (
                                 (newduration (ly:make-duration 4 2 1 1))
                                 (newtail (getListBack tail (quickParserB "16..")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

			((checkListStart tail (quickParserB "16."))
                          (let (
                                 (newduration (ly:make-duration 4 1 1 1))
                                 (newtail (getListBack tail (quickParserB "16.")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "16"))
                          (let (
                                 (newduration (ly:make-duration 4 0 1 1))
                                 (newtail (getListBack tail (quickParserB "16")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

                        ((checkListStart tail (quickParserB "32..."))
                          (let (
                                 (newduration (ly:make-duration 5 3 1 1))
                                 (newtail (getListBack tail (quickParserB "32...")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "32.."))
                          (let (
                                 (newduration (ly:make-duration 5 2 1 1))
                                 (newtail (getListBack tail (quickParserB "32..")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

			((checkListStart tail (quickParserB "32."))
                          (let (
                                 (newduration (ly:make-duration 5 1 1 1))
                                 (newtail (getListBack tail (quickParserB "32.")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "32"))
                          (let (
                                 (newduration (ly:make-duration 5 0 1 1))
                                 (newtail (getListBack tail (quickParserB "32")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

                        ((checkListStart tail (quickParserB "64..."))
                          (let (
                                 (newduration (ly:make-duration 6 3 1 1))
                                 (newtail (getListBack tail (quickParserB "64...")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "64.."))
                          (let (
                                 (newduration (ly:make-duration 6 2 1 1))
                                 (newtail (getListBack tail (quickParserB "64..")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

			((checkListStart tail (quickParserB "64."))
                          (let (
                                 (newduration (ly:make-duration 6 1 1 1))
                                 (newtail (getListBack tail (quickParserB "64.")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "64"))
                          (let (
                                 (newduration (ly:make-duration 6 0 1 1))
                                 (newtail (getListBack tail (quickParserB "64")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

                        ((checkListStart tail (quickParserB "2..."))
                          (let (
                                 (newduration (ly:make-duration 1 3 1 1))
                                 (newtail (getListBack tail (quickParserB "2...")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "2.."))
                          (let (
                                 (newduration (ly:make-duration 1 2 1 1))
                                 (newtail (getListBack tail (quickParserB "2..")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

			((checkListStart tail (quickParserB "2."))
                          (let (
                                 (newduration (ly:make-duration 1 1 1 1))
                                 (newtail (getListBack tail (quickParserB "2.")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "2"))
                          (let (
                                 (newduration (ly:make-duration 1 0 1 1))
                                 (newtail (getListBack tail (quickParserB "2")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

                        ((checkListStart tail (quickParserB "1..."))
                          (let (
                                 (newduration (ly:make-duration 0 3 1 1))
                                 (newtail (getListBack tail (quickParserB "1...")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "1.."))
                          (let (
                                 (newduration (ly:make-duration 0 2 1 1))
                                 (newtail (getListBack tail (quickParserB "1..")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

			((checkListStart tail (quickParserB "1."))
                          (let (
                                 (newduration (ly:make-duration 0 1 1 1))
                                 (newtail (getListBack tail (quickParserB "1.")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )
			((checkListStart tail (quickParserB "1"))
                          (let (
                                 (newduration (ly:make-duration 0 0 1 1))
                                 (newtail (getListBack tail (quickParserB "1")))
                               )
			   (quickGeneratorb newtail (if (null? currentMusicObject) 
                                                             currentMusicObject
                                                       (resetDurationsB currentMusicObject newduration)
                                                   ) delimCounter baseString currentString 
                                                     newduration currentMode data)
                          )
                        )

			(else (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data))
                     )
                    )
              )

	      

              (else (quickGeneratorb tail currentMusicObject 0 baseString currentString currentDuration currentMode data))
         )
    )
  )
 )
)
)




gtb = #(define-music-function (parser location data) (string?)
(let ((liste ( quickGeneratorb (quickParser (string-append data "    ")) '() 0 (length tuningb) (length tuningb)
                          (ly:make-duration 2 0 1 1) 'emptyMode '()) )
     )

(make-music 'SequentialMusic
        'elements
        liste)
)
)




tabImmo = #(define-music-function (parser location x) (ly:music?)
#{
  \new TabStaff {
      \set TabStaff.stringTunings = #tuning
      \set TabStaff.instrumentName = "Immo"
      {
       \override TabStaff.PaperColumn #'keep-inside-line = ##t
       \override Staff.TimeSignature #'stencil = ##f
%       \override Staff.BarLine #'stencil = ##f
       \override Staff.Clef #'stencil = ##f
       \override Score.BarNumber #'transparent = ##t
%       \override Score.SystemStartBar #'style = ##f
       \override Score.PaperColumn #'keep-inside-line = ##t
%       \override Staff.TextScript #'minimum-Y-extent = #'(-1 . 0)
%       \override Staff.Slur #'y-free = #0.4

       \time 400000/4
       { \halfNoteFix { $x } }
      }
  
    
  }
#})


tabGDGCFAD = #(define-music-function (parser location x) (ly:music?)
#{
  \new TabStaff {
      \set TabStaff.stringTunings = #tuning
      \set TabStaff.instrumentName = "+"
      {
       \override TabStaff.PaperColumn #'keep-inside-line = ##t
       \override Staff.TimeSignature #'stencil = ##f
%       \override Staff.BarLine #'stencil = ##f
       \override Staff.Clef #'stencil = ##f
%       \override Score.BarNumber #'transparent = ##t
%       \override Score.SystemStartBar #'style = ##f
       \override Score.PaperColumn #'keep-inside-line = ##t
%       \override Staff.TextScript #'minimum-Y-extent = #'(-1 . 0)
%       \override Staff.Slur #'y-free = #0.4

       \time 400000/4
       { \halfNoteFix { $x } }
      }
  
    
  }
#})

tabIlker = #(define-music-function (parser location x) (ly:music?)
#{
  \new TabStaff {
      \set TabStaff.stringTunings = #tuningb
      \set TabStaff.instrumentName = "Ilker"
      {
       \override TabStaff.PaperColumn #'keep-inside-line = ##t
       \override Staff.TimeSignature #'stencil = ##f
%       \override Staff.BarLine #'stencil = ##f
       \override Staff.Clef #'stencil = ##f
       \override Score.BarNumber #'transparent = ##t
%       \override Score.SystemStartBar #'style = ##f
       \override Score.PaperColumn #'keep-inside-line = ##t
%       \override Staff.TextScript #'minimum-Y-extent = #'(-1 . 0)
%       \override Staff.Slur #'y-free = #0.4

       \time 400000/4
       { \halfNoteFix { $x } }
      }
  }
#})

\include "tab-helper3.ly"
\include "tab-helper4.ly"
\include "tab-helper5.ly"

renderTabs = #(define-music-function (parser location x) (ly:music?)
#{
{
	\tabGCGCFAD { $x }                    
	\tabGDGCFAD { $x }                     
	\tabADGCFAD { $x } 
}           
#})

renderTabI = #(define-music-function (parser location x) (ly:music?)
#{
	\tabGCGCFAD {
				{ $x } 
	}                     
#})

nbr =  #(define-music-function (parser location x) (scheme?)
#{
	\mark \markup { \tiny { $x } }
#})

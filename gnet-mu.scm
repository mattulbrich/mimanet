;;; Lepton EDA netlister
;;; Copyright (C) 1998-2010 Ales Hvezda
;;; Copyright (C) 1998-2017 gEDA Contributors
;;; Copyright (C) 2018-2020 Lepton EDA Contributors
;;;
;;; This program is free software; you can redistribute it and/or modify
;;; it under the terms of the GNU General Public License as published by
;;; the Free Software Foundation; either version 2 of the License, or
;;; (at your option) any later version.
;;;
;;; This program is distributed in the hope that it will be useful,
;;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;; GNU General Public License for more details.
;;;
;;; You should have received a copy of the GNU General Public License
;;; along with this program; if not, write to the Free Software
;;; Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

;; --------------------------------------------------------------------------
;;
;; MU's exporter ...
;;

(use-modules (srfi srfi-1)
             (lepton object)
             (netlist schematic)
             (netlist package)
             (netlist schematic-component)
             (netlist schematic toplevel)
             (srfi srfi-1))

;;
;; Top level header
;;
(define (geda:write-top-header)
  (format #t ""))

;;; Top level component writing
(define (geda:components ls)
  ;; header for components section
  (display "START components\n\n")
  (for-each
   (lambda (package)
     (format #t "~A device=~A\n" package (get-device package)))
   ls)
  ;; footer for components section
  (display "\nEND components\n\n"))


;;; Graphical packages writing
(define (geda:graphicals ls)
  (define (no-refdes-component-info object)
    (format #f
            "Component without refdes: ~A at ~A"
            (component-basename object)
            (component-position object)))

  (define (graphical-info package)
    (or (schematic-component-refdes package)
        (no-refdes-component-info (schematic-component-object package))))

  ;; The graphical should not be a "no-connect" symbol.
  (define not-schematic-component-nc? (negate schematic-component-nc?))

  (let ((graphicals (filter not-schematic-component-nc? ls)))
    (if (null? graphicals)
        (display "No graphical symbols found\n\n")
        (format #t
                "START graphical symbols

~A
END graphical symbols

"
                (string-join (map graphical-info graphicals)
                             "\n"
                             'suffix)))))


;;; Renamed nets writing
(define (geda:renamed-nets ls)
  (define source first)
  (define destination second)
  ;; header for renamed section
  (display "START renamed-nets\n\n")
  (for-each
   (lambda (renamed-pair)
     (format #t
             "~A -> ~A\n"
             (source renamed-pair)
             (destination renamed-pair)))
   ls)
  ;; footer for renamed section
  (display "\nEND renamed-nets\n\n"))


;;; Returns formatted list of CONNECTIONS as a string.
(define (connections->string connections)
  (define package car)
  (define pinnumber cdr)
  (define (connection->string connection)
    (format #f "~A ~A" (package connection) (pinnumber connection)))
  (string-join (map connection->string connections) ", "))


;;; Displays formatted output of NETNAMES and their connections
;;; where each connection is a package-pin pair. Each output line
;;; looks like:
;;;   netname : package pin, package pin, ...
(define (geda:write-net netnames)
  (for-each
   (lambda (netname)
     (format #t "~A : ~A\n"
             netname
             (connections->string (get-all-connections netname))))
   netnames))

;;; Write the net part of the gEDA format
(define (geda:nets nets)
  ;; header for nets section
  (display "START nets\n\n")
  (geda:write-net nets)
  ;; footer for net section
  (display "\nEND nets\n\n"))

;;; Write "no-connect" nets
(define (no-connect-nets ls)
  (if (null? ls)
      (display "No \"no-connect\" nets found\n\n")
      (format #t
              "START \"no-connect\" nets

~A
END \"no-connect\" nets

"
              (string-join ls "\n" 'suffix))))

(define (maskNewline s)
  (let ((lst (map (lambda (x) (if (char=? #\nl x) "\\n" (string x)))
                  (string->list s))))
    (string-join lst "")))

;;; Highest level function
;;; Write my special testing netlist format
;;;
(define (mu output-filename)

  ;;
  ;; Print the components and the attributes.
  (let ((components (schematic-components (toplevel-schematic))))
    (for-each
     (lambda (c)
       (display "Component ")
       (display (schematic-component-refdes c))
       (display "\n")
       (for-each 
        (lambda (a)
          (display "  .")
          (display (symbol->string (car a)))
          (display " = ")
          (display (maskNewline (cadr a)))
          (display "\n"))        
        (filter (lambda (x) (not (eq? (car x) (string->symbol "refdes"))))
                (schematic-component-attribs c))))
     components))

  ;;
  ;; Print the nets
  (for-each
   (lambda (n)     
      (format #t "Net ~A : ~A\n"
             n (connections->string (get-all-connections n))))   
   (schematic-nets (toplevel-schematic))))
  
  ;; (geda:write-top-header)
  ;; (geda:graphicals (schematic-graphicals (toplevel-schematic)))
  ;; (geda:components (schematic-package-names (toplevel-schematic)))
  ;; (no-connect-nets (schematic-nc-nets (toplevel-schematic)))
  ;; (geda:renamed-nets (gnetlist:get-renamed-nets "dummy"))
  ;; (geda:nets (schematic-nets (toplevel-schematic))))

;;
;; gEDA's native test netlist format specific functions ends
;;
;; --------------------------------------------------------------------------

;; Copyright (c) 2012, David J. Pearce (djp@ecs.vuw.ac.nz)
;; All rights reserved.
;;
;; Redistribution and use in source and binary forms, with or without
;; modification, are permitted provided that the following conditions are met:
;;    * Redistributions of source code must retain the above copyright
;;      notice, this list of conditions and the following disclaimer.
;;    * Redistributions in binary form must reproduce the above copyright
;;      notice, this list of conditions and the following disclaimer in the
;;      documentation and/or other materials provided with the distribution.
;;    * Neither the name of the <organization> nor the
;;      names of its contributors may be used to endorse or promote products
;;      derived from this software without specific prior written permission.
;;
;; THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
;; ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
;; WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
;; DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
;; DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
;; (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
;; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
;; ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
;; (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
;; SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

(require 'cc-mode)

(defun wyrl-comment-dwim (arg)
  "Comment or uncomment current line or region in a smart way.
For detail, see `comment-dwim'."
   (interactive "*P")
   (require 'newcomment)
   (let ((deactivate-mark nil) (comment-start "//") (comment-end ""))
     (comment-dwim arg)))

(defvar wyrl-keywords
  '("include" "term" "define" "reduce" "infer" "requires" "name" "rank" "function" "if" "in" "let" "as" "false" "true" "package")
    "Wyrl keywords.")

(defvar wyrl-types
  '("real" "int" "bool" "void" "string")
  "Wyrl types.")

(defvar wyrl-keywords-regexp (regexp-opt wyrl-keywords 'words))
(defvar wyrl-type-regexp (regexp-opt wyrl-types 'words))

(setq wyrl-font-lock-keywords
  `(
    (,wyrl-type-regexp . font-lock-type-face)
    (,wyrl-keywords-regexp . font-lock-keyword-face)
))

(define-derived-mode wyrl-mode fundamental-mode
  "wyrl mode"
  "Major mode for editing wyrl files..."
  (setq font-lock-defaults '((wyrl-font-lock-keywords)))
  (setq wyrl-keywords-regexp nil)
  (setq wyrl-types-regexp nil)

  ;; borrow adaptive fill for comments from cc-mode
  (substitute-key-definition 'fill-paragraph 'c-fill-paragraph
			     c-mode-base-map global-map)

  ;; java-style comments "// ..." and “/* … */”
  (define-key wyrl-mode-map [remap comment-dwim] 'wyrl-comment-dwim)
  (modify-syntax-entry ?\/ ". 124b" wyrl-mode-syntax-table)
  (modify-syntax-entry ?* ". 23" wyrl-mode-syntax-table)
  (modify-syntax-entry ?\n "> b" wyrl-mode-syntax-table)

  ;; indentation.  Needs work!
  (setq indent-tabs-mode nil)
  (local-set-key (kbd "TAB") 'tab-to-tab-stop)
  (setq tab-stop-list (list 4 8 12 16 20 24 28))

  ;; unicode characters
  (local-set-key "\M-u" '(lambda () (interactive) (ucs-insert #x222A)))
  (local-set-key "\M-n" '(lambda () (interactive) (ucs-insert #x2229)))
  (local-set-key "\M-e" '(lambda () (interactive) (ucs-insert #x2208)))
)

;; automode
(setq auto-mode-alist (cons '("\\.wyrl\\'" . wyrl-mode) auto-mode-alist))

(provide 'wyrl)

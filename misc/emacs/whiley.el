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

(defun whiley-comment-dwim (arg)
  "Comment or uncomment current line or region in a smart way.
For detail, see `comment-dwim'."
   (interactive "*P")
   (require 'newcomment)
   (let ((deactivate-mark nil) (comment-start "//") (comment-end ""))
     (comment-dwim arg)))

(defvar whiley-keywords
  '("native" "export" "extern" "null" "return" "if" "is" "throw" "throws" "try" "catch" "switch" "case" "default" "break" "continue" "skip" "do" "while" "for" "else" "define" "assume" "assert" "assume" "package" "import" "function" "method" "type" "constant" "from" "debug" "where" "ensures" "requires" "public" "protected" "private" "this" "str" "new" "in" "no" "some" "all" "false" "true")
    "Whiley keywords.")

(defvar whiley-types
  '("real" "int" "bool" "void" "void" "ref")
  "Whiley types.")

(defvar whiley-keywords-regexp (regexp-opt whiley-keywords 'words))
(defvar whiley-type-regexp (regexp-opt whiley-types 'words))

(setq whiley-font-lock-keywords
  `(
    (,whiley-type-regexp . font-lock-type-face)
    (,whiley-keywords-regexp . font-lock-keyword-face)
))

(define-derived-mode whiley-mode fundamental-mode
  "whiley mode"
  "Major mode for editing Whiley ..."
  (setq font-lock-defaults '((whiley-font-lock-keywords)))
  (setq whiley-keywords-regexp nil)
  (setq whiley-types-regexp nil)

  ;; borrow adaptive fill for comments from cc-mode
  (substitute-key-definition 'fill-paragraph 'c-fill-paragraph
			     c-mode-base-map global-map)

  ;; java-style comments "// ..." and “/* … */”
  (define-key whiley-mode-map [remap comment-dwim] 'whiley-comment-dwim)
  (modify-syntax-entry ?\/ ". 124b" whiley-mode-syntax-table)
  (modify-syntax-entry ?* ". 23" whiley-mode-syntax-table)
  (modify-syntax-entry ?\n "> b" whiley-mode-syntax-table)

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
(setq auto-mode-alist (cons '("\\.whiley\\'" . whiley-mode) auto-mode-alist))

(provide 'whiley)

; ==================================================================
; BEGIN WHILEY MODE
; ==================================================================

(defun whiley-comment-dwim (arg)
  "Comment or uncomment current line or region in a smart way.
For detail, see `comment-dwim'."
   (interactive "*P")
   (require 'newcomment)
   (let ((deactivate-mark nil) (comment-start "//") (comment-end ""))
     (comment-dwim arg)))

(defvar whiley-keywords
  '("process" "native" "extern" "null" "return" "if" "is" "throw" "throws" "switch" "case" "default" "break" "skip" "while" "for" "else" "define" "assert" "as" "package" "import" "from" "debug" "where" "ensures" "requires" "public" "protected" "private" "export" "this" "str" "spawn" "in" "no" "some" "false" "true")
    "Whiley keywords.")

(defvar whiley-types
  '("real" "int" "bool" "void" "string" "char" "void" "process")
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

  ;; c-style comment "// ..."
  (define-key whiley-mode-map [remap comment-dwim] 'whiley-comment-dwim)
  (modify-syntax-entry ?\/ ". 12b" whiley-mode-syntax-table)
  (modify-syntax-entry ?\n "> b" whiley-mode-syntax-table)

  ;; Indentation.  Needs work!
  (setq indent-tabs-mode nil)
  (local-set-key (kbd "TAB") 'tab-to-tab-stop)
  (setq tab-stop-list (list 4 8 12 16 20 24 28))  

  ;; unicode characters
  (local-set-key "\M-u" '(lambda () (interactive) (ucs-insert #x222A)))
  (local-set-key "\M-n" '(lambda () (interactive) (ucs-insert #x2229)))
  (local-set-key "\M-e" '(lambda () (interactive) (ucs-insert #x2208)))
)

; ==================================================================
; END WHILEY MODE
; ==================================================================

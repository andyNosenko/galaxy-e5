#@category PCode
#
# Exports decompiled pseudocode for functions referenced by specific string keywords.
#
# Usage (headless):
#   analyzeHeadless <project_dir> <project_name> -import <exe_path> \
#     -scriptPath "<this_script_dir>" -postScript export_uninstall_logic.py "<out_dir>"
#
# Notes:
# - This script is designed to capture "uninstall/overlay" logic, not every function in the binary.
# - Output is intentionally plain-text C-like decompiler output.

import os

from ghidra.app.decompiler import DecompInterface
from ghidra.app.decompiler import DecompileOptions
from ghidra.util.task import ConsoleTaskMonitor
from ghidra.program.model.listing import Function

keywords = [
    # Uninstaller / overlay
    "uninstallOverlay",
    "installOverlay",
    "/vendor/overlay/",
    "Overlay is broken",
    "idmap",
    # Package manager commands
    "pm uninstall -k",
    "cmd package uninstall -k",
    "uninstall [-k] PACKAGE",
    "pm uninstall -k",
    # deployagent / fastdeploy bootstrap
    "deployagent",
    "com.android.fastdeploy.DeployAgent",
    "exec app_process",
    "/data/local/tmp/deployagent.jar",
    # filesystem/permission related
    "chmod 777",
    "remount",
    "remount_shell",
    "/system/bin/sh",
    "DeleteFileW",
    "RemoveDirectoryW",
    # product identifiers seen in strings
    "installer.Uninstaller",
    "Uninstaller.kt",
]


def _as_bytes(s):
    # Search by raw ASCII bytes (Ghidra's findBytes works on byte arrays).
    # Native images may include null-terminated/UTF-8, but ASCII keywords still match.
    return bytearray([b for b in s.encode("ascii", "ignore")])


def _decompile_function(di, func, monitor):
    # Decompile a single function and return text (or None).
    opts = DecompileOptions()
    res = di.decompileFunction(func, 60, monitor, opts)
    if res is None:
        return None
    if not res.decompileCompleted():
        return None
    df = res.getDecompiledFunction()
    if df is None:
        return None
    try:
        return df.getC()
    except Exception:
        return str(df)


def run():
    args = getScriptArgs()
    out_dir = args[0] if len(args) >= 1 else "_decompile/starshine_1.8.0/ghidra_export"
    out_dir = os.path.abspath(out_dir)
    if not os.path.exists(out_dir):
        os.makedirs(out_dir)

    monitor = ConsoleTaskMonitor()

    listing = currentProgram.getListing()
    fm = currentProgram.getFunctionManager()
    refman = currentProgram.getReferenceManager()

    # Init decompiler interface (needs to be opened on current program)
    di = DecompInterface()
    di.setSimplificationStyle("decompile")  # best-effort output
    di.openProgram(currentProgram)

    # Collect candidate functions (by xref to occurrences of keywords)
    func_set = set()

    mem = currentProgram.getMemory()
    start = mem.getMinAddress()
    end = mem.getMaxAddress()

    for kw in keywords:
        try:
            b = _as_bytes(kw)
            found_addrs = mem.findBytes(b, start, end)
            # findBytes yields addresses iterable-like in Jython
            for addr in found_addrs:
                # xrefs to this address
                refs = refman.getReferencesTo(addr)
                for r in refs:
                    from_addr = r.getFromAddress()
                    f = fm.getFunctionContaining(from_addr)
                    if f is not None:
                        func_set.add(f.getEntryPoint())
        except Exception:
            # Keep going; some keywords may not exist in the binary or findBytes may fail.
            continue

    # Decompile each collected function.
    func_list = list(func_set)
    func_list.sort(key=lambda a: a.toString())

    out_manifest = os.path.join(out_dir, "decompiled_functions_manifest.txt")
    with open(out_manifest, "w") as mf:
        mf.write("Decompiled function entry points: %d\n" % len(func_list))
        for ep in func_list:
            mf.write(str(ep) + "\n")

    for idx, ep in enumerate(func_list):
        try:
            func = fm.getFunctionAt(ep)
            if func is None:
                continue
            c = _decompile_function(di, func, monitor)
            if c is None:
                continue
            fname = os.path.join(out_dir, "func_%04d_%s.c" % (idx, ep.toString().replace(":", "_")))
            with open(fname, "w") as f:
                f.write(c)
        except Exception:
            continue


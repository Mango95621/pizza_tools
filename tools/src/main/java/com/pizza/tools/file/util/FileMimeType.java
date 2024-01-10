package com.pizza.tools.file.util;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.pizza.tools.log.LogTool;
import com.pizza.tools.file.FileTool;

import java.util.HashMap;
import java.util.Locale;

/**
 * @author BoWei
 * 2023/8/23 19:26
 * 1. getMimeType(str: String?) 先用`mimeTables`获取`mimeType`, 如果为空再去`android.webkit.MimeTypeMap`中找
 * 2. getMimeType(uri: Uri?) 先用`getFilePathByUri(uri)`将`uri`转换为`path`, 再执行步骤1
 */
public class FileMimeType {

    /**
     * 常见的文件 MimeType
     */
    private static final HashMap<String, String> MIME_TYPE_MAP = new HashMap() {{
        put("%", "application/x-trash");
        put("323", "text/h323");
        put("3g2", "video/3gpp2");
        put("3ga", "audio/3gpp");
        put("3gp", "video/3gpp");
        put("3gp2", "video/3gpp2");
        put("3gpp", "video/3gpp");
        put("3gpp2", "video/3gpp2");
        put("7z", "application/x-7z-compressed");
        put("a52", "audio/ac3");
        put("aac", "audio/aac");
        put("ac3", "audio/ac3");
        put("adt", "audio/aac");
        put("adts", "audio/aac");
        put("i", "application/postscript");
        put("if", "audio/x-aiff");
        put("ifc", "audio/x-aiff");
        put("iff", "audio/x-aiff");
        put("lc", "chemical/x-alchemy");
        put("mr", "audio/amr");
        put("nx", "application/annodex");
        put("pk", "application/vnd.android.package-archive");
        put("ppcache", "text/cache-manifest");
        put("pplication", "application/x-ms-application");
        put("rt", "image/x-jg");
        put("arw", "image/x-sony-arw");
        put("asc", "text/plain");
        put("asf", "video/x-ms-asf");
        put("asn", "chemical/x-ncbi-asn1-spec");
        put("aso", "chemical/x-ncbi-asn1-binary");
        put("asx", "video/x-ms-asf");
        put("a,m", "application/a,m+xml");
        put("a,mcat", "application/a,mcat+xml");
        put("a,msrv", "application/a,mserv+xml");
        put("au", "audio/basic");
        put("avi", "video/avi");
        put("awb", "audio/amr-wb");
        put("axa", "audio/annodex");
        put("axv", "video/annodex");
        put("b", "chemical/x-molconn-Z");
        put("bak", "application/x-trash");
        put("bat", "application/x-msdos-program");
        put("bcpio", "application/x-bcpio");
        put("bib", "text/x-bibtex");
        put("bin", "application/octet-stream");
        put("bmp", "image/x-ms-bmp");
        put("boo", "text/x-boo");
        put("book", "application/x-maker");
        put("brf", "text/plain");
        put("bsd", "chemical/x-crossfire");
        put("c", "text/x-csrc");
        put("c++", "text/x-c++src");
        put("c3d", "chemical/x-chem3d");
        put("cab", "application/x-cab");
        put("cac", "chemical/x-cache");
        put("cache", "chemical/x-cache");
        put("cap", "application/vnd.tcpdump.pcap");
        put("cascii", "chemical/x-cactvs-binary");
        put("cat", "application/vnd.ms-pki.seccat");
        put("cbin", "chemical/x-cactvs-binary");
        put("cbr", "application/x-cbr");
        put("cbz", "application/x-cbz");
        put("cc", "text/x-c++src");
        put("cda", "application/x-cdf");
        put("cdf", "application/x-cdf");
        put("cdr", "image/x-coreldraw");
        put("cdt", "image/x-coreldrawtemplate");
        put("cdx", "chemical/x-cdx");
        put("cdy", "application/vnd.cinderella");
        put("cef", "chemical/x-cxf");
        put("cer", "application/pkix-cert");
        put("chm", "chemical/x-chemdraw");
        put("chrt", "application/x-kchart");
        put("cif", "chemical/x-cif");
        put("class", "application/java-vm");
        put("cls", "text/x-tex");
        put("cmdf", "chemical/x-cmdf");
        put("cml", "chemical/x-cml");
        put("cod", "application/vnd.rim.cod");
        put("com", "application/x-msdos-program");
        put("cpa", "chemical/x-compass");
        put("cpio", "application/x-cpio");
        put("cpp", "text/x-c++src");
        put("cpt", "image/x-corelpho,paint");
        put("cr2", "image/x-canon-cr2");
        put("crl", "application/x-pkcs7-crl");
        put("crt", "application/x-x509-ca-cert");
        put("crw", "image/x-canon-crw");
        put("csd", "audio/csound");
        put("csf", "chemical/x-cache-csf");
        put("csh", "text/x-csh");
        put("csm", "chemical/x-csml");
        put("csml", "chemical/x-csml");
        put("css", "text/css");
        put("csv", "text/comma-separated-values");
        put("ctab", "chemical/x-cactvs-binary");
        put("ctx", "chemical/x-ctx");
        put("cu", "application/cu-seeme");
        put("cub", "chemical/x-gaussian-cube");
        put("cur", "image/ico");
        put("cxf", "chemical/x-cxf");
        put("cxx", "text/x-c++src");
        put("d", "text/x-dsrc");
        put("davmount", "application/davmount+xml");
        put("dcm", "application/dicom");
        put("dcr", "application/x-direc,r");
        put("ddeb", "application/vnd.debian.binary-package");
        put("deb", "application/x-debian-package");
        put("deploy", "application/octet-stream");
        put("dfxp", "application/ttml+xml");
        put("dif", "video/dv");
        put("diff", "text/plain");
        put("dir", "application/x-direc,r");
        put("djv", "image/vnd.djvu");
        put("djvu", "image/vnd.djvu");
        put("dl", "video/dl");
        put("dll", "application/x-msdos-program");
        put("dmg", "application/x-apple-diskimage");
        put("dms", "application/x-dms");
        put("dng", "image/x-adobe-dng");
        put("doc", "application/msword");
        put("docm", "application/vnd.ms-word.document.macroEnabled.12");
        put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        put("dot", "application/msword");
        put("dotm", "application/vnd.ms-word.template.macroEnabled.12");
        put("dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template");
        put("dv", "video/dv");
        put("dvi", "application/x-dvi");
        put("dx", "chemical/x-jcamp-dx");
        put("dxr", "application/x-direc,r");
        put("emb", "chemical/x-embl-dl-nucleotide");
        put("embl", "chemical/x-embl-dl-nucleotide");
        put("eml", "message/rfc822");
        put("ent", "chemical/x-pdb");
        put("eot", "application/vnd.ms-fon,bject");
        put("eps", "application/postscript");
        put("eps2", "application/postscript");
        put("eps3", "application/postscript");
        put("epsf", "application/postscript");
        put("epsi", "application/postscript");
        put("epub", "application/epub+zip");
        put("erf", "image/x-epson-erf");
        put("es", "application/ecmascript");
        put("etx", "text/x-setext");
        put("exe", "application/x-msdos-program");
        put("ez", "application/andrew-inset");
        put("f4a", "audio/mp4");
        put("f4b", "audio/mp4");
        put("f4p", "audio/mp4");
        put("f4v", "video/mp4");
        put("fb", "application/x-maker");
        put("fbdoc", "application/x-maker");
        put("fch", "chemical/x-gaussian-checkpoint");
        put("fchk", "chemical/x-gaussian-checkpoint");
        put("fig", "application/x-xfig");
        put("fl", "application/x-android-drm-fl");
        put("flac", "audio/flac");
        put("fli", "video/fli");
        put("flv", "video/x-flv");
        put("fm", "application/x-maker");
        put("frame", "application/x-maker");
        put("frm", "application/x-maker");
        put("gal", "chemical/x-gaussian-log");
        put("gam", "chemical/x-gamess-input");
        put("gamin", "chemical/x-gamess-input");
        put("gan", "application/x-ganttproject");
        put("gau", "chemical/x-gaussian-input");
        put("gcd", "text/x-pcs-gcd");
        put("gcf", "application/x-graphing-calcula,r");
        put("gcg", "chemical/x-gcg8-sequence");
        put("gen", "chemical/x-genbank");
        put("gf", "application/x-tex-gf");
        put("gif", "image/gif");
        put("gjc", "chemical/x-gaussian-input");
        put("gjf", "chemical/x-gaussian-input");
        put("gl", "video/gl");
        put("gnumeric", "application/x-gnumeric");
        put("gpt", "chemical/x-mopac-graph");
        put("gsf", "application/x-font");
        put("gsm", "audio/x-gsm");
        put("gtar", "application/x-gtar");
        put("gz", "application/gzip");
        put("h", "text/x-chdr");
        put("h++", "text/x-c++hdr");
        put("hdf", "application/x-hdf");
        put("heic", "image/heic");
        put("heics", "image/heic-sequence");
        put("heif", "image/heif");
        put("heifs", "image/heif-sequence");
        put("hh", "text/x-c++hdr");
        put("hif", "image/heif");
        put("hin", "chemical/x-hin");
        put("hpp", "text/x-c++hdr");
        put("hqx", "application/mac-binhex40");
        put("hs", "text/x-haskell");
        put("hta", "application/hta");
        put("htc", "text/x-component");
        put("htm", "text/html");
        put("html", "text/html");
        put("hwp", "application/x-hwp");
        put("hxx", "text/x-c++hdr");
        put("ica", "application/x-ica");
        put("ice", "x-conference/x-cooltalk");
        put("ico", "image/x-icon");
        put("ics", "text/calendar");
        put("icz", "text/calendar");
        put("ief", "image/ief");
        put("iges", "model/iges");
        put("igs", "model/iges");
        put("iii", "application/x-iphone");
        put("imy", "audio/imelody");
        put("info", "application/x-info");
        put("inp", "chemical/x-gamess-input");
        put("ins", "application/x-internet-signup");
        put("iso", "application/x-iso9660-image");
        put("isp", "application/x-internet-signup");
        put("ist", "chemical/x-isostar");
        put("istr", "chemical/x-isostar");
        put("jad", "text/vnd.sun.j2me.app-descrip,r");
        put("jam", "application/x-jam");
        put("jar", "application/java-archive");
        put("java", "text/x-java");
        put("jdx", "chemical/x-jcamp-dx");
        put("jmz", "application/x-jmol");
        put("jng", "image/x-jng");
        put("jnlp", "application/x-java-jnlp-file");
        put("jp2", "image/jp2");
        put("jpe", "image/jpeg");
        put("jpeg", "image/jpeg");
        put("jpf", "image/jpx");
        put("jpg", "image/jpeg");
        put("jpg2", "image/jp2");
        put("jpm", "image/jpm");
        put("jpx", "image/jpx");
        put("js", "application/javascript");
        put("json", "application/json");
        put("jsonld", "application/ld+json");
        put("kar", "audio/midi");
        put("key", "application/pgp-keys");
        put("kil", "application/x-killustra,r");
        put("kin", "chemical/x-kinemage");
        put("kml", "application/vnd.google-earth.kml+xml");
        put("kmz", "application/vnd.google-earth.kmz");
        put("kpr", "application/x-kpresenter");
        put("kpt", "application/x-kpresenter");
        put("ksp", "application/x-kspread");
        put("kwd", "application/x-kword");
        put("kwt", "application/x-kword");
        put("latex", "application/x-latex");
        put("lha", "application/x-lha");
        put("lhs", "text/x-literate-haskell");
        put("lin", "application/bbolin");
        put("lrc", "application/lrc");
        put("lsf", "video/x-la-asf");
        put("lsx", "video/x-la-asf");
        put("ltx", "text/x-tex");
        put("ly", "text/x-lilypond");
        put("lyx", "application/x-lyx");
        put("lzh", "application/x-lzh");
        put("lzx", "application/x-lzx");
        put("m1v", "video/mpeg");
        put("m2t", "video/mpeg");
        put("m2ts", "video/mp2t");
        put("m2v", "video/mpeg");
        put("m3g", "application/m3g");
        put("m3u", "audio/x-mpegurl");
        put("m3u8", "audio/x-mpegurl");
        put("m4a", "audio/mpeg");
        put("m4b", "audio/mp4");
        put("m4p", "audio/mp4");
        put("m4r", "audio/mpeg");
        put("m4v", "video/mp4");
        put("maker", "application/x-maker");
        put("man", "application/x-troff-man");
        put("manifest", "application/x-ms-manifest");
        put("markdown", "text/markdown");
        put("mbox", "application/mbox");
        put("mcif", "chemical/x-mmcif");
        put("mcm", "chemical/x-macmolecule");
        put("md", "text/markdown");
        put("mdb", "application/msaccess");
        put("me", "application/x-troff-me");
        put("mesh", "model/mesh");
        put("mid", "audio/midi");
        put("midi", "audio/midi");
        put("mif", "application/x-mif");
        put("mjs", "application/javascript");
        put("mka", "audio/x-matroska");
        put("mkv", "video/x-matroska");
        put("mm", "application/x-freemind");
        put("mmd", "chemical/x-macromodel-input");
        put("mmf", "application/vnd.smaf");
        put("mml", "text/mathml");
        put("mmod", "chemical/x-macromodel-input");
        put("mng", "video/x-mng");
        put("mobi", "application/x-mobipocket-ebook");
        put("moc", "text/x-moc");
        put("mol", "chemical/x-mdl-molfile");
        put("mol2", "chemical/x-mol2");
        put("moo", "chemical/x-mopac-out");
        put("mop", "chemical/x-mopac-input");
        put("mopcrt", "chemical/x-mopac-input");
        put("mov", "video/quicktime");
        put("movie", "video/x-sgi-movie");
        put("mp1", "audio/mpeg");
        put("mp1v", "video/mpeg");
        put("mp2", "audio/mpeg");
        put("mp2v", "video/mpeg");
        put("mp3", "audio/mpeg");
        put("mp4", "video/mp4");
        put("mp4v", "video/mp4");
        put("mpa", "audio/mpeg");
        put("mpc", "chemical/x-mopac-input");
        put("mpe", "video/mpeg");
        put("mpeg", "video/mpeg");
        put("mpeg1", "video/mpeg");
        put("mpeg2", "video/mpeg");
        put("mpeg4", "video/mp4");
        put("mpega", "audio/mpeg");
        put("mpg", "video/mpeg");
        put("mpga", "audio/mpeg");
        put("mph", "application/x-comsol");
        put("mpv", "video/x-matroska");
        put("mpv1", "video/mpeg");
        put("mpv2", "video/mpeg");
        put("ms", "application/x-troff-ms");
        put("msh", "model/mesh");
        put("msi", "application/x-msi");
        put("msp", "application/octet-stream");
        put("msu", "application/octet-stream");
        put("mts", "video/mp2t");
        put("mvb", "chemical/x-mopac-vib");
        put("mxf", "application/mxf");
        put("mxmf", "audio/mobile-xmf");
        put("mxu", "video/vnd.mpegurl");
        put("nb", "application/mathematica");
        put("nbp", "application/mathematica");
        put("nc", "application/x-netcdf");
        put("nef", "image/x-nikon-nef");
        put("nrw", "image/x-nikon-nrw");
        put("nwc", "application/x-nwc");
        put("o", "application/x-object");
        put("oda", "application/oda");
        put("odb", "application/vnd.oasis.opendocument.database");
        put("odc", "application/vnd.oasis.opendocument.chart");
        put("odf", "application/vnd.oasis.opendocument.formula");
        put("odg", "application/vnd.oasis.opendocument.graphics");
        put("odi", "application/vnd.oasis.opendocument.image");
        put("odm", "application/vnd.oasis.opendocument.text-master");
        put("odp", "application/vnd.oasis.opendocument.presentation");
        put("ods", "application/vnd.oasis.opendocument.spreadsheet");
        put("odt", "application/vnd.oasis.opendocument.text");
        put("oga", "audio/ogg");
        put("ogg", "audio/ogg");
        put("ogv", "video/ogg");
        put("ogx", "application/ogg");
        put("old", "application/x-trash");
        put("one", "application/onenote");
        put("onepkg", "application/onenote");
        put("onetmp", "application/onenote");
        put("one,c2", "application/onenote");
        put("opf", "application/oebps-package+xml");
        put("opus", "audio/ogg");
        put("orc", "audio/csound");
        put("orf", "image/x-olympus-orf");
        put("ota", "application/vnd.android.ota");
        put("otf", "font/ttf");
        put("otg", "application/vnd.oasis.opendocument.graphics-template");
        put("oth", "application/vnd.oasis.opendocument.text-web");
        put("otp", "application/vnd.oasis.opendocument.presentation-template");
        put("ots", "application/vnd.oasis.opendocument.spreadsheet-template");
        put("ott", "application/vnd.oasis.opendocument.text-template");
        put("oza", "application/x-oz-application");
        put("p", "text/x-pascal");
        put("p12", "application/x-pkcs12");
        put("p7r", "application/x-pkcs7-certreqresp");
        put("pac", "application/x-ns-proxy-au,config");
        put("pas", "text/x-pascal");
        put("pat", "image/x-coreldrawpattern");
        put("patch", "text/x-diff");
        put("pbm", "image/x-portable-bitmap");
        put("pcap", "application/vnd.tcpdump.pcap");
        put("pcf", "application/x-font");
        put("pcf.Z", "application/x-font-pcf");
        put("pcx", "image/pcx");
        put("pdb", "chemical/x-pdb");
        put("pdf", "application/pdf");
        put("pef", "image/x-pentax-pef");
        put("pem", "application/x-pem-file");
        put("pfa", "application/x-font");
        put("pfb", "application/x-font");
        put("pfr", "application/font-tdpfr");
        put("pfx", "application/x-pkcs12");
        put("pgm", "image/x-portable-graymap");
        put("pgn", "application/x-chess-pgn");
        put("pgp", "application/pgp-signature");
        put("phps", "text/text");
        put("pk", "application/x-tex-pk");
        put("pl", "text/x-perl");
        put("pls", "audio/x-scpls");
        put("pm", "text/x-perl");
        put("png", "image/png");
        put("pnm", "image/x-portable-anymap");
        put("po", "text/plain");
        put("pot", "application/vnd.ms-powerpoint");
        put("potm", "application/vnd.ms-powerpoint.template.macroEnabled.12");
        put("potx", "application/vnd.openxmlformats-officedocument.presentationml.template");
        put("ppam", "application/vnd.ms-powerpoint.addin.macroEnabled.12");
        put("ppm", "image/x-portable-pixmap");
        put("pps", "application/vnd.ms-powerpoint");
        put("ppsm", "application/vnd.ms-powerpoint.slideshow.macroEnabled.12");
        put("ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow");
        put("ppt", "application/vnd.ms-powerpoint");
        put("pptm", "application/vnd.ms-powerpoint.presentation.macroEnabled.12");
        put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        put("prc", "application/x-mobipocket-ebook");
        put("prf", "application/pics-rules");
        put("prt", "chemical/x-ncbi-asn1-ascii");
        put("ps", "application/postscript");
        put("psd", "image/x-pho,shop");
        put("py", "text/x-python");
        put("pyc", "application/x-python-code");
        put("pyo", "application/x-python-code");
        put("qgs", "application/x-qgis");
        put("qt", "video/quicktime");
        put("qtl", "application/x-quicktimeplayer");
        put("ra", "audio/x-pn-realaudio");
        put("raf", "image/x-fuji-raf");
        put("ram", "audio/x-pn-realaudio");
        put("rar", "application/rar");
        put("ras", "image/x-cmu-raster");
        put("rb", "application/x-ruby");
        put("rd", "chemical/x-mdl-rdfile");
        put("rdf", "application/rdf+xml");
        put("rdp", "application/x-rdp");
        put("rgb", "image/x-rgb");
        put("rm", "audio/x-pn-realaudio");
        put("roff", "application/x-troff");
        put("ros", "chemical/x-rosdal");
        put("rpm", "application/x-redhat-package-manager");
        put("rss", "application/rss+xml");
        put("rtf", "text/rtf");
        put("rtttl", "audio/midi");
        put("rtx", "audio/midi");
        put("rw2", "image/x-panasonic-rw2");
        put("rxn", "chemical/x-mdl-rxnfile");
        put("scala", "text/x-scala");
        put("sce", "application/x-scilab");
        put("sci", "application/x-scilab");
        put("sco", "audio/csound");
        put("scr", "application/x-silverlight");
        put("sct", "text/scriptlet");
        put("sd", "chemical/x-mdl-sdfile");
        put("sd2", "audio/x-sd2");
        put("sda", "application/vnd.stardivision.draw");
        put("sdc", "application/vnd.stardivision.calc");
        put("sdd", "application/vnd.stardivision.impress");
        put("sdf", "chemical/x-mdl-sdfile");
        put("sdp", "application/vnd.stardivision.impress");
        put("sds", "application/vnd.stardivision.chart");
        put("sdw", "application/vnd.stardivision.writer");
        put("ser", "application/java-serialized-object");
        put("sfd", "application/vnd.font-fontforge-sfd");
        put("sfv", "text/x-sfv");
        put("sgf", "application/x-go-sgf");
        put("sgl", "application/vnd.stardivision.writer-global");
        put("sh", "text/x-sh");
        put("shar", "application/x-shar");
        put("shp", "application/x-qgis");
        put("shtml", "text/html");
        put("shx", "application/x-qgis");
        put("sid", "audio/prs.sid");
        put("sig", "application/pgp-signature");
        put("sik", "application/x-trash");
        put("silo", "model/mesh");
        put("sis", "application/vnd.symbian.install");
        put("sisx", "x-epoc/x-sisx-app");
        put("sit", "application/x-stuffit");
        put("sitx", "application/x-stuffit");
        put("skd", "application/x-koan");
        put("skm", "application/x-koan");
        put("skp", "application/x-koan");
        put("skt", "application/x-koan");
        put("sldm", "application/vnd.ms-powerpoint.slide.macroEnabled.12");
        put("sldx", "application/vnd.openxmlformats-officedocument.presentationml.slide");
        put("smf", "audio/sp-midi");
        put("smi", "application/smil+xml");
        put("smil", "application/smil+xml");
        put("snd", "audio/basic");
        put("spc", "chemical/x-galactic-spc");
        put("spl", "application/x-futuresplash");
        put("spx", "audio/ogg");
        put("sql", "application/x-sql");
        put("src", "application/x-wais-source");
        put("srt", "application/x-subrip");
        put("srw", "image/x-samsung-srw");
        put("stc", "application/vnd.sun.xml.calc.template");
        put("std", "application/vnd.sun.xml.draw.template");
        put("sti", "application/vnd.sun.xml.impress.template");
        put("stl", "application/vnd.ms-pki.stl");
        put("stw", "application/vnd.sun.xml.writer.template");
        put("sty", "text/x-tex");
        put("sv4cpio", "application/x-sv4cpio");
        put("sv4crc", "application/x-sv4crc");
        put("svg", "image/svg+xml");
        put("svgz", "image/svg+xml");
        put("sw", "chemical/x-swissprot");
        put("swf", "application/x-shockwave-flash");
        put("swfl", "application/x-shockwave-flash");
        put("sxc", "application/vnd.sun.xml.calc");
        put("sxd", "application/vnd.sun.xml.draw");
        put("sxg", "application/vnd.sun.xml.writer.global");
        put("sxi", "application/vnd.sun.xml.impress");
        put("sxm", "application/vnd.sun.xml.math");
        put("sxw", "application/vnd.sun.xml.writer");
        put("t", "application/x-troff");
        put("tar", "application/x-tar");
        put("taz", "application/x-gtar-compressed");
        put("tcl", "text/x-tcl");
        put("tex", "text/x-tex");
        put("texi", "application/x-texinfo");
        put("texinfo", "application/x-texinfo");
        put("text", "text/plain");
        put("tgf", "chemical/x-mdl-tgf");
        put("tgz", "application/x-gtar-compressed");
        put("thmx", "application/vnd.ms-officetheme");
        put("tif", "image/tiff");
        put("tiff", "image/tiff");
        put("tk", "text/x-tcl");
        put("tm", "text/texmacs");
        put(");rrent", "application/x-bit,rrent");
        put("tr", "application/x-troff");
        put("ts", "video/mp2ts");
        put("tsp", "application/dsptype");
        put("tsv", "text/tab-separated-values");
        put("ttc", "font/collection");
        put("ttf", "font/ttf");
        put("ttl", "text/turtle");
        put("ttml", "application/ttml+xml");
        put("txt", "text/plain");
        put("udeb", "application/x-debian-package");
        put("uls", "text/iuls");
        put("ustar", "application/x-ustar");
        put("val", "chemical/x-ncbi-asn1-binary");
        put("vcard", "text/vcard");
        put("vcd", "application/x-cdlink");
        put("vcf", "text/x-vcard");
        put("vcs", "text/x-vcalendar");
        put("vmd", "chemical/x-vmd");
        put("vms", "chemical/x-vamas-iso14976");
        put("vor", "application/vnd.stardivision.writer");
        put("vrm", "x-world/x-vrml");
        put("vrml", "x-world/x-vrml");
        put("vsd", "application/vnd.visio");
        put("vss", "application/vnd.visio");
        put("vst", "application/vnd.visio");
        put("vsw", "application/vnd.visio");
        put("wad", "application/x-doom");
        put("wasm", "application/wasm");
        put("wav", "audio/x-wav");
        put("wax", "audio/x-ms-wax");
        put("wbmp", "image/vnd.wap.wbmp");
        put("wbxml", "application/vnd.wap.wbxml");
        put("webarchive", "application/x-webarchive");
        put("webarchivexml", "application/x-webarchive-xml");
        put("webm", "video/webm");
        put("webp", "image/webp");
        put("wk", "application/x-123");
        put("wm", "video/x-ms-wm");
        put("wma", "audio/x-ms-wma");
        put("wmd", "application/x-ms-wmd");
        put("wml", "text/vnd.wap.wml");
        put("wmlc", "application/vnd.wap.wmlc");
        put("wmls", "text/vnd.wap.wmlscript");
        put("wmlsc", "application/vnd.wap.wmlscriptc");
        put("wmv", "video/x-ms-wmv");
        put("wmx", "video/x-ms-wmx");
        put("wmz", "application/x-ms-wmz");
        put("woff", "font/woff");
        put("woff2", "font/woff2");
        put("wp5", "application/vnd.wordperfect5.1");
        put("wpd", "application/vnd.wordperfect");
        put("wpl", "application/vnd.ms-wpl");
        put("wrf", "video/x-webex");
        put("wrl", "x-world/x-vrml");
        put("wsc", "text/scriptlet");
        put("wvx", "video/x-ms-wvx");
        put("wz", "application/x-wingz");
        put("x3d", "model/x3d+xml");
        put("x3db", "model/x3d+binary");
        put("x3dv", "model/x3d+vrml");
        put("xbm", "image/x-xbitmap");
        put("xcf", "application/x-xcf");
        put("xcos", "application/x-scilab-xcos");
        put("xht", "application/xhtml+xml");
        put("xhtml", "application/xhtml+xml");
        put("xlam", "application/vnd.ms-excel.addin.macroEnabled.12");
        put("xlb", "application/vnd.ms-excel");
        put("xls", "application/vnd.ms-excel");
        put("xlsb", "application/vnd.ms-excel.sheet.binary.macroEnabled.12");
        put("xlsm", "application/vnd.ms-excel.sheet.macroEnabled.12");
        put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        put("xlt", "application/vnd.ms-excel");
        put("xltm", "application/vnd.ms-excel.template.macroEnabled.12");
        put("xltx", "application/vnd.openxmlformats-officedocument.spreadsheetml.template");
        put("xmf", "audio/midi");
        put("xml", "text/xml");
        put("xpi", "application/x-xpinstall");
        put("xpm", "image/x-xpixmap");
        put("xsd", "application/xml");
        put("xsl", "application/xslt+xml");
        put("xslt", "application/xslt+xml");
        put("xspf", "application/xspf+xml");
        put("xtel", "chemical/x-xtel");
        put("xul", "application/vnd.mozilla.xul+xml");
        put("xwd", "image/x-xwindowdump");
        put("xyz", "chemical/x-xyz");
        put("xz", "application/x-xz");
        put("yt", "video/vnd.youtube.yt");
        put("zip", "application/zip");
        put("zmt", "chemical/x-mopac-input");
        put("~", "application/x-trash");
    }};


    public String getMimeType(Uri uri) {
        if (uri == null) {
            return "application/octet-stream";
        }
        return getMimeType(FileTool.get().getFileUriUtil().getPathByUri(uri));
    }

    /**
     * 根据 File Name/Path/Url 获取相应的 MimeType
     *
     * @param path eg: xxx.jpg ; xxx/xxx.jpg ; http://xxx.jpg
     * @return mineType  "application/x-flac" , "video/3gpp" ...
     */
    public String getMimeType(String path) {
        String type = "application/octet-stream";
        if (TextUtils.isEmpty(path)) {
            return type;
        }
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        String mimeType = getMimeTypeSupplement(path);
        if (mimeType == null) {
            String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            mimeType = mimeTypeFromExtension != null ? mimeTypeFromExtension : type;
        }
        LogTool.i(FileTool.TAG, "FileMimeType：extension=" + extension + " mimeType=" + mimeType);
        return mimeType.toLowerCase(Locale.getDefault());
    }

    /** 1. https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Basics_of_HTTP/MIME_types
     *    https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
     *
     * 2. https://www.sitepoint.com/mime-types-complete-list/
     *
     * 3. https://github.com/broofa/mime/blob/master/types
     *
     * 4. https://github.com/zhanghai/MaterialFiles
     */
    public String getMimeTypeSupplement(String extension) {
        return MIME_TYPE_MAP.get(extension);
    }

}
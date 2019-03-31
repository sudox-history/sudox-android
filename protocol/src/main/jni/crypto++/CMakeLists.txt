add_library(crypto++ STATIC
        crypto++/cryptlib.cpp
        crypto++/cpu.cpp
        crypto++/integer.cpp
        crypto++/3way.cpp
        crypto++/adler32.cpp
        crypto++/algebra.cpp
        crypto++/algparam.cpp
        crypto++/arc4.cpp
        crypto++/aria-simd.cpp
        crypto++/aria.cpp
        crypto++/ariatab.cpp
        crypto++/asn.cpp
        crypto++/authenc.cpp
        crypto++/base32.cpp
        crypto++/base64.cpp
        crypto++/basecode.cpp
        crypto++/bfinit.cpp
        crypto++/blake2-simd.cpp
        crypto++/blake2.cpp
        crypto++/blowfish.cpp
        crypto++/blumshub.cpp
        crypto++/camellia.cpp
        crypto++/cast.cpp
        crypto++/casts.cpp
        crypto++/cbcmac.cpp
        crypto++/ccm.cpp
        crypto++/chacha.cpp
        crypto++/channels.cpp
        crypto++/cmac.cpp
        crypto++/crc-simd.cpp
        crypto++/crc.cpp
        crypto++/default.cpp
        crypto++/des.cpp
        crypto++/dessp.cpp
        crypto++/dh.cpp
        crypto++/dh2.cpp
        crypto++/dll.cpp
        crypto++/dsa.cpp
        crypto++/eax.cpp
        crypto++/ec2n.cpp
        crypto++/eccrypto.cpp
        crypto++/ecp.cpp
        crypto++/elgamal.cpp
        crypto++/emsa2.cpp
        crypto++/eprecomp.cpp
        crypto++/esign.cpp
        crypto++/files.cpp
        crypto++/filters.cpp
        crypto++/fips140.cpp
        crypto++/fipstest.cpp
        crypto++/gcm-simd.cpp
        crypto++/gcm.cpp
        crypto++/gf256.cpp
        crypto++/gf2_32.cpp
        crypto++/gf2n.cpp
        crypto++/gfpcrypt.cpp
        crypto++/gost.cpp
        crypto++/gzip.cpp
        crypto++/hex.cpp
        crypto++/hmac.cpp
        crypto++/hrtimer.cpp
        crypto++/ida.cpp
        crypto++/idea.cpp
        crypto++/iterhash.cpp
        crypto++/kalyna.cpp
        crypto++/kalynatab.cpp
        crypto++/keccak.cpp
        crypto++/luc.cpp
        crypto++/mars.cpp
        crypto++/marss.cpp
        crypto++/md2.cpp
        crypto++/md4.cpp
        crypto++/md5.cpp
        crypto++/misc.cpp
        crypto++/modes.cpp
        crypto++/mqueue.cpp
        crypto++/mqv.cpp
        crypto++/nbtheory.cpp
        crypto++/neon-simd.cpp
        crypto++/network.cpp
        crypto++/oaep.cpp
        # No idea where this file is meant to come from
        # ospstore.cpp
        crypto++/osrng.cpp
        crypto++/panama.cpp
        crypto++/pkcspad.cpp
        crypto++/poly1305.cpp
        crypto++/polynomi.cpp
        crypto++/pssr.cpp
        crypto++/pubkey.cpp
        crypto++/queue.cpp
        crypto++/rabin.cpp
        crypto++/randpool.cpp
        crypto++/rc2.cpp
        crypto++/rc5.cpp
        crypto++/rc6.cpp
        crypto++/rdrand.cpp
        crypto++/rdtables.cpp
        crypto++/rijndael.cpp
        crypto++/ripemd.cpp
        crypto++/rng.cpp
        crypto++/rsa.cpp
        crypto++/rw.cpp
        crypto++/safer.cpp
        crypto++/salsa.cpp
        crypto++/scrypt.cpp
        crypto++/seal.cpp
        crypto++/seed.cpp
        crypto++/serpent.cpp
        crypto++/sha-simd.cpp
        crypto++/sha.cpp
        crypto++/sha3.cpp
        crypto++/shacal2-simd.cpp
        crypto++/shacal2.cpp
        crypto++/shark.cpp
        crypto++/sharkbox.cpp
        crypto++/skipjack.cpp
        crypto++/socketft.cpp
        crypto++/sosemanuk.cpp
        crypto++/square.cpp
        crypto++/squaretb.cpp
        crypto++/strciphr.cpp
        crypto++/tea.cpp
        crypto++/tftables.cpp
        crypto++/threefish.cpp
        crypto++/tiger.cpp
        crypto++/tigertab.cpp
        crypto++/trdlocal.cpp
        crypto++/ttmac.cpp
        crypto++/twofish.cpp
        crypto++/vmac.cpp
        crypto++/wait.cpp
        crypto++/wake.cpp
        crypto++/whrlpool.cpp
        crypto++/xtr.cpp
        crypto++/xtrcrypt.cpp
        crypto++/zdeflate.cpp
        crypto++/zinflate.cpp
        crypto++/zlib.cpp

        # i586 only
        crypto++/rijndael-simd.cpp
        # AESNI or ARMv7a/ARMv8a available
        crypto++/sse-simd.cpp
        crypto++/pem-com.cpp
        crypto++/pem-rd.cpp
        crypto++/pem-wr.cpp
    )

target_include_directories(crypto++ PUBLIC .)
target_compile_options(crypto++ PRIVATE -fPIC)

if(${CMAKE_CXX_COMPILER_ID} STREQUAL "GNU")
    target_compile_options(crypto++ PRIVATE
            -Wno-class-memaccess
            -Wno-unknown-pragmas
        )
elseif(${CMAKE_CXX_COMPILER_ID} STREQUAL "Clang")
    target_compile_options(crypto++ PRIVATE
            -Wno-keyword-macro
            -Wno-unused-const-variable
            -Wno-unused-private-field
        )
endif()

if("${ANDROID_ABI}" STREQUAL "")
    # This assumes that we're going with x86 like systems if we're not building Android
    target_compile_options(crypto++
        PUBLIC -maes -mpclmul -msha -msse4.1 -msse4.2 -mssse3)
    install(TARGETS crypto++ LIBRARY DESTINATION lib ARCHIVE DESTINATION lib)
else()
    include(AndroidNdkModules)
    android_ndk_import_module_cpufeatures()
    target_link_libraries(crypto++ cpufeatures)
    if(${ANDROID_ABI} STREQUAL "x86_64")
        target_compile_options(crypto++ PUBLIC -maes -mpclmul -msha -msse4.1 -msse4.2 -mssse3)
    elseif(${ANDROID_ABI} STREQUAL "x86")
        target_compile_options(crypto++ PUBLIC -maes -mpclmul -msha -msse4.1 -msse4.2 -mssse3)
    elseif(${ANDROID_ABI} STREQUAL "armeabi-v7a")
        # Setting NEON only really makes sense for this ABI
        target_compile_options(crypto++ PUBLIC -mfpu=neon)
    elseif(${ANDROID_ABI} STREQUAL "arm64-v8a")
        # There isn't anything that we need to do for this architecture
    else()
        message(FATAL_ERROR "Can't determine android architecture " ${ANDROID_ABI})
    endif()
endif()


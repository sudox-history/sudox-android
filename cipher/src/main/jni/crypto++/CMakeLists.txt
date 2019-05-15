add_library(crypto++ STATIC
        crypto++/cryptlib.cpp
        crypto++/cpu.cpp
        crypto++/integer.cpp
        crypto++/algebra.cpp
        crypto++/safer.cpp
        crypto++/serpent.cpp
        crypto++/chacha.cpp
        crypto++/md4.cpp
        crypto++/3way.cpp
        crypto++/keccak.cpp
        crypto++/files.cpp
        crypto++/filters.cpp
        crypto++/eax.cpp
        crypto++/panama.cpp
        crypto++/kalynatab.cpp
        crypto++/ripemd.cpp
        crypto++/keccak_simd.cpp
        crypto++/dessp.cpp
        crypto++/gf2n.cpp
        crypto++/hex.cpp
        crypto++/ttmac.cpp
        crypto++/xtrcrypt.cpp
        crypto++/blake2.cpp
        crypto++/arc4.cpp
        crypto++/vmac.cpp
        crypto++/misc.cpp
        crypto++/randpool.cpp
        crypto++/luc.cpp
        crypto++/ccm.cpp
        crypto++/threefish.cpp
        crypto++/cmac.cpp
        crypto++/rabin.cpp
        crypto++/seal.cpp
        crypto++/shacal2.cpp
        crypto++/rdtables.cpp
        crypto++/sha.cpp
        crypto++/modes.cpp
        crypto++/blake2s_simd.cpp
        crypto++/hc256.cpp
        crypto++/skipjack.cpp
#        crypto++/ppc_power9.cpp
        crypto++/donna_32.cpp
        crypto++/square.cpp
        crypto++/gzip.cpp
        crypto++/ecp.cpp
        crypto++/rdrand.cpp
        crypto++/rsa.cpp
        crypto++/nbtheory.cpp
        crypto++/mqueue.cpp
        crypto++/base64.cpp
        crypto++/seed.cpp
        crypto++/shacal2_simd.cpp
        crypto++/xtr.cpp
        crypto++/gf2_32.cpp
        crypto++/authenc.cpp
        crypto++/marss.cpp
        crypto++/sha3.cpp
        crypto++/des.cpp
        crypto++/rc2.cpp
        crypto++/osrng.cpp
        crypto++/blowfish.cpp
        crypto++/lea_simd.cpp
#        crypto++/ppc_power8.cpp
        crypto++/mqv.cpp
        crypto++/squaretb.cpp
        crypto++/pssr.cpp
        crypto++/iterhash.cpp
        crypto++/dh.cpp
        crypto++/donna_sse.cpp
        crypto++/bfinit.cpp
        crypto++/darn.cpp
        crypto++/camellia.cpp
        crypto++/gf2n_simd.cpp
        crypto++/channels.cpp
        crypto++/rc6.cpp
        crypto++/sharkbox.cpp
        crypto++/polynomi.cpp
        crypto++/rijndael.cpp
        crypto++/zinflate.cpp
        crypto++/simon128_simd.cpp
        crypto++/queue.cpp
        crypto++/salsa.cpp
        crypto++/kalyna.cpp
        crypto++/rc5.cpp
        crypto++/sm3.cpp
        crypto++/sha_simd.cpp
        crypto++/rng.cpp
        crypto++/emsa2.cpp
        crypto++/casts.cpp
        crypto++/sm4_simd.cpp
        crypto++/eccrypto.cpp
        crypto++/gf256.cpp
        crypto++/twofish.cpp
        crypto++/dsa.cpp
        crypto++/gcm.cpp
        crypto++/hight.cpp
        crypto++/elgamal.cpp
        crypto++/fips140.cpp
        crypto++/crc_simd.cpp
        crypto++/rabbit.cpp
        crypto++/donna_64.cpp
        crypto++/aria.cpp
        crypto++/fipstest.cpp
        crypto++/md5.cpp
        crypto++/xed25519.cpp
        crypto++/simeck_simd.cpp
        crypto++/hc128.cpp
        crypto++/wake.cpp
        crypto++/dh2.cpp
        crypto++/tiger.cpp
        crypto++/cham_simd.cpp
        crypto++/pubkey.cpp
        crypto++/simon64_simd.cpp
        crypto++/tea.cpp
        crypto++/default.cpp
        crypto++/base32.cpp
        crypto++/zlib.cpp
        crypto++/lea.cpp
        crypto++/mars.cpp
        crypto++/rw.cpp
        crypto++/crc.cpp
        crypto++/asn.cpp
        crypto++/keccak_core.cpp
        crypto++/ida.cpp
        crypto++/dll.cpp
        crypto++/padlkrng.cpp
        crypto++/tftables.cpp
        crypto++/speck64_simd.cpp
        crypto++/oaep.cpp
        crypto++/sse_simd.cpp
#        crypto++/chacha_avx.cpp
        crypto++/rijndael_simd.cpp
        crypto++/cbcmac.cpp
        crypto++/tigertab.cpp
        crypto++/chacha_simd.cpp
        crypto++/cast.cpp
        crypto++/neon_simd.cpp
        crypto++/ariatab.cpp
        crypto++/blumshub.cpp
        crypto++/algparam.cpp
        crypto++/hrtimer.cpp
        crypto++/shark.cpp
        crypto++/pkcspad.cpp
        crypto++/cham.cpp
        crypto++/strciphr.cpp
        crypto++/idea.cpp
#        crypto++/ppc_simd.cpp
        crypto++/speck128_simd.cpp
        crypto++/sm4.cpp
        crypto++/shake.cpp
        crypto++/eprecomp.cpp
        crypto++/simon.cpp
        crypto++/gost.cpp
        crypto++/simeck.cpp
#        crypto++/ppc_power7.cpp
        crypto++/chachapoly.cpp
        crypto++/zdeflate.cpp
        crypto++/adler32.cpp
        crypto++/md2.cpp
        crypto++/tweetnacl.cpp
        crypto++/whrlpool.cpp
        crypto++/blake2b_simd.cpp
        crypto++/ec2n.cpp
        crypto++/esign.cpp
        crypto++/hmac.cpp
        crypto++/poly1305.cpp
        crypto++/gfpcrypt.cpp
        crypto++/speck.cpp
        crypto++/scrypt.cpp
        crypto++/sosemanuk.cpp
        crypto++/basecode.cpp
        crypto++/aria_simd.cpp
        crypto++/gcm_simd.cpp
        crypto++/pem-com.cpp
        crypto++/pem-rd.cpp
        crypto++/pem-wr.cpp)

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
        target_compile_definitions(crypto++ PRIVATE CRYPTOPP_DISABLE_MIXED_ASM)
    elseif(${ANDROID_ABI} STREQUAL "x86")
        target_compile_options(crypto++ PUBLIC -maes -mpclmul -msha -msse4.1 -msse4.2 -mssse3)
        target_compile_definitions(crypto++ PRIVATE CRYPTOPP_DISABLE_MIXED_ASM)
    elseif(${ANDROID_ABI} STREQUAL "armeabi-v7a")
        # Setting NEON only really makes sense for this ABI
        target_compile_options(crypto++ PUBLIC -mfpu=neon)
    elseif(${ANDROID_ABI} STREQUAL "arm64-v8a")
        # There isn't anything that we need to do for this architecture
    else()
        message(FATAL_ERROR "Can't determine android architecture " ${ANDROID_ABI})
    endif()
endif()


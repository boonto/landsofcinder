#ifdef GL_ES
#define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform sampler2D u_texShadow;
//uniform sampler2D u_texParticles;

void main() {
    vec4 color = texture2D(u_texture, v_texCoords);
    vec4 shadow = texture2D(u_texShadow, v_texCoords);
    //vec4 particles = texture2D(u_texParticles, v_texCoords);

    gl_FragColor = color - shadow;

    gl_FragColor.a = 1.0; //um depth anzeigen zu können
}
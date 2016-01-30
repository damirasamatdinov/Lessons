precision mediump float;
uniform sampler2D u_Texture;
uniform vec3 u_LightPos;

varying vec3 v_Position;
varying vec3 v_Normal;
varying vec2 v_TexCoordinate;

void main() {
    float distance = length(u_LightPos - v_Position);

    vec3 lightVector = normalize(u_LightPos - v_Position);

    float diffuce = max(dot(v_Normal,lightVector),0.0);

    diffuce = diffuce * (1.0/distance);

    diffuce = diffuce + 0.2;

    gl_FragColor = (diffuce * texture2D(u_Texture,v_TexCoordinate));
}
